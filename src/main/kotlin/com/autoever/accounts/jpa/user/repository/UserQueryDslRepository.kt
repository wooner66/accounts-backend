package com.autoever.accounts.jpa.user.repository

import com.autoever.accounts.jpa.user.QUser.user
import com.autoever.accounts.jpa.user.User
import com.autoever.accounts.jpa.user.condition.UserSearchCondition
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

interface UserQueryDslRepository {
    fun findAllByCondition(condition: UserSearchCondition, pageable: Pageable): Page<User>
	fun findAllByIdBetween(startId: Long, endId: Long, pageable: Pageable): Page<User>
}

class UserQueryDslRepositoryImpl(
	private val jpaQueryFactory: JPAQueryFactory,
) : UserQueryDslRepository {
    override fun findAllByCondition(condition: UserSearchCondition, pageable: Pageable): Page<User> {
        val builder = BooleanBuilder().apply {
            if (condition.userIds.isNotEmpty()) {
                and(user.id.`in`(condition.userIds))
            }

            condition.username?.let { username ->
                and(user.username.like("%$username%"))
            }
            condition.name?.let { name ->
                and(user.name.like("%$name%"))
            }
            condition.phone?.let { phone ->
                and(user.phone.like("%$phone%"))
            }
        }

        val results = jpaQueryFactory
			.selectFrom(user)
            .where(builder)
            .orderBy(user.id.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total = jpaQueryFactory
			.select(user.id.count())
            .from(user)
            .where(builder)
            .fetchOne() ?: 0L

        return PageImpl(
            results,
            pageable,
            total
        )
    }

	override fun findAllByIdBetween(startId: Long, endId: Long, pageable: Pageable): Page<User> {
		val results = jpaQueryFactory
			.selectFrom(user)
			.where(user.id.between(startId, endId))
			.orderBy(user.id.asc())
			.offset(pageable.offset)
			.limit(pageable.pageSize.toLong())
			.fetch()

		val total = jpaQueryFactory
			.select(user.id.count())
			.from(user)
			.where(user.id.between(startId, endId))
			.fetchOne() ?: 0L

		return PageImpl(
			results,
			pageable,
			total
		)
	}
}
