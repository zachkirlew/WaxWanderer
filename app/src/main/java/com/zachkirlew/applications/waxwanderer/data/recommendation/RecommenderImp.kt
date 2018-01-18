package com.zachkirlew.applications.waxwanderer.data.recommendation

import android.content.Context
import com.recombee.api_client.api_requests.AddPurchase
import com.recombee.api_client.api_requests.DeletePurchase
import com.recombee.api_client.api_requests.RecommendUsersToUser
import com.zachkirlew.applications.waxwanderer.util.ConfigHelper
import io.reactivex.Observable
import io.reactivex.Single
import org.joda.time.DateTime

class RecommenderImp (context: Context) : Recommender{


    private var recombeeClient : RecombeeClient

    init {
        val apiIdentifier = ConfigHelper.getConfigValue(context, "api_identifier")
        val secretToken = ConfigHelper.getConfigValue(context, "secret_token")

        recombeeClient = RecombeeSingleton.getInstance(apiIdentifier, secretToken)
    }

    override fun addFavourite(userId: String, itemId: String): Single<String> {
        return Single.create({ e ->

            val jodaDate = DateTime()
            val date = jodaDate.toDate()

            recombeeClient.send(AddPurchase(userId, itemId)
                    .setTimestamp(date)
                    .setCascadeCreate(true))


            e.onSuccess("Added")
        })
    }

    override fun removeFavourite(userId: String, itemId: String): Single<String> {
        return Single.create({ e ->

            recombeeClient.send(DeletePurchase(userId, itemId))

            e.onSuccess("Delete")
        })
    }

    override fun recommendUserToUser(userId: String, count: Long) : Observable<List<String>> {
        return Observable.create ({e->

            val response = recombeeClient.send(RecommendUsersToUser(userId, count))
            val idList = response.ids.toList()

            e.onNext(idList)
            e.onComplete()
        })
    }


}