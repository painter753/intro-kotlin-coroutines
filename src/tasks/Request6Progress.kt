package tasks

import contributors.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.atomic.AtomicInteger

suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) = coroutineScope {
    val repos = service
        .getOrgRepos(req.org)
        //.execute() // Executes request and blocks the current thread
        .also { logRepos(req, it) }
        .bodyList()

    var resultList = emptyList<User>()
    val count = AtomicInteger(repos.size)

    repos.map { repo ->
        service
            .getRepoContributors(req.org, repo.name)
            //.execute() // Executes request and blocks the current thread
            .also { logUsers(repo, it) }
            .bodyList()
            .also {
                resultList = resultList + it
                val c = count.decrementAndGet()
                updateResults(resultList.aggregate(), c == 0)
            }
    }
}
