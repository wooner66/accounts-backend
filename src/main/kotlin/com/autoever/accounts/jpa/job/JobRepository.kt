package com.autoever.accounts.jpa.job

import org.springframework.data.jpa.repository.JpaRepository

interface MessageJobRepository : JpaRepository<MessageJob, Long>

interface JobChunkRepository : JpaRepository<JobChunk, Long>

interface SendLogRepository : JpaRepository<SendLog, Long>
