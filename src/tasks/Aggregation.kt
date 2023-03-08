package tasks

import contributors.User

/*
TODO: Write aggregation code.

 In the initial list each user is present several times, once for each
 repository he or she contributed to.
 Merge duplications: each user should be present only once in the resulting list
 with the total value of contributions for all the repositories.
 Users should be sorted in a descending order by their contributions.

 The corresponding test can be found in test/tasks/AggregationKtTest.kt.
 You can use 'Navigate | Test' menu action (note the shortcut) to navigate to the test.
*/
fun List<User>.aggregate(): List<User> =
    this.groupBy { it.login }.map { User(it.key, it.value.sumOf { it.contributions } ) }.sortedByDescending { it.contributions }

// solution 1
fun List<User>.aggregateSolution(): List<User> =
    groupBy { it.login }
        // тут можно сразу использовать деструктуризацию для более понятного именования
        .map { (login, group) -> User(login, group.sumOf { it.contributions }) }
        .sortedByDescending { it.contributions }
// solution 2
// использовать groupingBy
// fun List<User>.aggregateSolution2(): List<User> =
//    groupingBy { it.login }

