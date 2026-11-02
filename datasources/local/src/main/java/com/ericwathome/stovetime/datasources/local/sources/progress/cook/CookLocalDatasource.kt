package com.ericwathome.stovetime.datasources.local.sources.progress.cook

interface CookLocalDatasource {
    val saveCook: SaveCookLocalDatasource
    val rateCook: RateCookLocalDatasource
    val observeCooks: ObserveCooksLocalDatasource
    val deleteAllCooks: DeleteAllCooksLocalDatasource
    val getCookedRecipeIds: GetCookedRecipeIdsLocalDatasource
}

internal class CookLocalDatasourceImpl(
    override val saveCook: SaveCookLocalDatasource,
    override val rateCook: RateCookLocalDatasource,
    override val observeCooks: ObserveCooksLocalDatasource,
    override val deleteAllCooks: DeleteAllCooksLocalDatasource,
    override val getCookedRecipeIds: GetCookedRecipeIdsLocalDatasource,
) : CookLocalDatasource
