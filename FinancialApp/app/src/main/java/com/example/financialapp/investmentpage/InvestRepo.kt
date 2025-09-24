package com.example.financialapp.investmentpage

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InvestRepo(
    private val dao: InvestDao
) {
    fun observePortfolio(): Flow<List<Investments>> =
        dao.observeAll().map { list -> list.map(::entityToDomain) }

    suspend fun getAll(): List<Investments> =
        dao.getAll().map(::entityToDomain)

    suspend fun add(investment: Investments) {
        dao.upsert(domainToEntity(investment))
    }

    suspend fun delete(investment: Investments) {
        val match = dao.getAll().firstOrNull {
            it.symbol == investment.nameInvest &&
                    it.shares == investment.shares &&
                    it.price  == investment.price &&
                    it.addedAt == investment.date
        } ?: return
        dao.delete(match)
    }

    suspend fun clear() = dao.clear()

    private fun entityToDomain(e: InvestEntity) = Investments(
        nameInvest = e.symbol,
        shares     = e.shares,
        price      = e.price,
        date       = e.addedAt
    )

    private fun domainToEntity(d: Investments) = InvestEntity(
        symbol  = d.nameInvest.trim().uppercase(),
        shares  = d.shares,
        price   = d.price,
        addedAt = d.date
    )
}