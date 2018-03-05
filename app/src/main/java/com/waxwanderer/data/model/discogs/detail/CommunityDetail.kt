package com.waxwanderer.data.model.discogs.detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CommunityDetail : Serializable {

    @SerializedName("contributors")
    @Expose
    var contributors: List<Contributor>? = null
    @SerializedName("data_quality")
    @Expose
    var dataQuality: String? = null
    @SerializedName("have")
    @Expose
    var have: Int? = null
    @SerializedName("rating")
    @Expose
    var rating: Rating? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
    @SerializedName("submitter")
    @Expose
    var submitter: Submitter? = null
    @SerializedName("want")
    @Expose
    var want: Int? = null

}