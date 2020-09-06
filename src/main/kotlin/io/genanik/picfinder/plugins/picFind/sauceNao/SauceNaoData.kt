package io.genanik.picfinder.plugins.picFind.sauceNao

data class SauceNaoData (
    val results: List<SauceResults>,
    val header: SauceNaoHeaders
)

data class SauceResults(
    val data: SauceResultData,
    val header: SauceResultHeader
)

data class SauceResultData(
    val author_name: String?,
    val title: String?,
    val ext_urls: List<String>,
    val da_id: Long,
    val author_url: String
)

data class SauceResultHeader(
    val index_id: Int,
    val similarity: String,
    val thumbnail: String,
    val index_name: String
)


data class SauceNaoHeaders(
    val query_image_display: String,
//    val index:
    val results_returned: Int,
    val minimum_similarity: Double,
    val account_type: Int,
    val short_limit: String,
    val long_remaining: Int,
    val user_id: Int,
    val results_requested: Int,
    val query_image: String,
    val long_limit: String,
    val search_depth: String,
    val short_remaining: Int,
    val status: Int
)

