package tasks

import contributors.*
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

suspend fun loadContributorsChannels(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    coroutineScope {
        val channel = Channel<List<User>>()
        val repos = service
            .getOrgRepos(req.org)
            //.execute() // Executes request and blocks the current thread
            .also { logRepos(req, it) }
            .bodyList()



        repos.map { repo ->
            async {
                service.getRepoContributors(req.org, repo.name)
                    .also { logUsers(repo, it) }
                    .bodyList()
                    .also {
                        channel.send(it)
                    }
            }
        }

        launch {

            var result = emptyList<User>()
            repeat(repos.size) {
                val userList = channel.receive()
                result = result + userList
                updateResults(result.aggregate(), it == repos.lastIndex)
            }
        }

    }
}
