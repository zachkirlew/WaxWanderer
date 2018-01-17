package com.zachkirlew.applications.waxwanderer.data.recommendation

class FallbackRecommender {

    fun calculateJaccardSimilarity(a: List<String>, b: List<String>): Double {

        val s1 = HashSet<String>()
        a.indices.mapTo(s1) { a[it] }

        val s2 = HashSet<String>()
        b.indices.mapTo(s2) { b[it] }

        val sa = s1.size
        val sb = s2.size

        s1.retainAll(s2)

        val intersection = s1.size
        return 1.0 / (sa + sb - intersection) * intersection
    }
}