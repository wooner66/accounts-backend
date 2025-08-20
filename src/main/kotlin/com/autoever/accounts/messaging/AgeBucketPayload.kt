package com.autoever.accounts.messaging

/**
 * ageFrom, ageTo : 나이 범위(예: 20..29). ageTo == null 이면 "ageFrom 이상" (90+)
 * message: 전송할 본문
 */
data class AgeBucketPayload(
	val jobId: Long,
	val ageFrom: Int,
	val ageTo: Int?, // null이면 ageFrom 이상
	val message: String
)
