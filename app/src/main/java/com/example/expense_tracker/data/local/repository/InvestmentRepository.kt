package com.example.expense_tracker.data.local.repository

import com.example.expense_tracker.data.local.dao.InvestmentDao
import com.example.expense_tracker.data.model.InvestmentEntity
import com.example.expense_tracker.domain.model.AssetClass
import com.example.expense_tracker.domain.model.Investment
import com.example.expense_tracker.domain.repository.IInvestmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// R-4: Data-layer implementation; transforms InvestmentEntity ↔ Investment domain model
class InvestmentRepository(private val investmentDao: InvestmentDao) : IInvestmentRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override suspend fun add(investment: Investment): Long =
        investmentDao.insert(investment.toEntity())

    override suspend fun update(investment: Investment) =
        investmentDao.update(investment.toEntity())

    override suspend fun delete(investment: Investment) =
        investmentDao.delete(investment.toEntity())

    override suspend fun getById(id: Long): Investment? =
        investmentDao.getById(id)?.toDomain()

    override fun getAll(): Flow<List<Investment>> =
        investmentDao.getAllFlow().map { list -> list.map { it.toDomain() } }

    // R-4: Filter by asset class — passes enum name as string to DAO
    override fun getByAssetClass(assetClass: AssetClass): Flow<List<Investment>> =
        investmentDao.getByAssetClassFlow(assetClass.name).map { list -> list.map { it.toDomain() } }

    private fun Investment.toEntity() = InvestmentEntity(
        id = id,
        name = name,
        asset_class = assetClass.name,
        invested_amount = investedAmount,
        current_value = currentValue,
        date = date.format(dateFormatter)
    )

    private fun InvestmentEntity.toDomain() = Investment(
        id = id,
        name = name,
        assetClass = AssetClass.valueOf(asset_class),
        investedAmount = invested_amount,
        currentValue = current_value,
        date = LocalDate.parse(date, dateFormatter)
    )
}
