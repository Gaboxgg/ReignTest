package com.example.reigntest

import com.google.gson.annotations.SerializedName

data class HitPojo (@SerializedName ("created_at") var created_at:String="",
                    @SerializedName("title") var title:String="",
                    @SerializedName("url") var url:String="",
                    @SerializedName("author") var author:String="",
                    @SerializedName  ("points") var points:String="",
                    @SerializedName  ("story_text") var storyText:String="",
                    @SerializedName  ("comment_text") var commentText:String="",
                    @SerializedName  ("num_comments") var num_comments:String="",
                    @SerializedName  ("story_id") var story_id:String="",
                    @SerializedName  ("story_title") var story_title:String="",
                    @SerializedName  ("story_url") var story_url:String="",
                    @SerializedName  ("parent_id") var parent_id:String="",
                    @SerializedName  ("created_at_i") var created_at_i:String="",
                   /* @SerializedName  ("_tags") var _tags:String,*/
                    @SerializedName  ("objectID") var objectID:String=""
                   /* @SerializedName  ("_highlightResult") var _highlightResult:String*/
)