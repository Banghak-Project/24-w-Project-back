package org.project.exchange.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Slf4j
@RequiredArgsConstructor
@Component
public class BatchScheduler {

	private final JobLauncher jobLauncher;
	private final Job fetchCurrencyJob;

	private boolean jobSucceeded = false;

	/**
	 * 평일 09:00-18:59 동안 50초 간격
	 * JVM Time-Zone → Asia/Seoul
	 */
	@Scheduled(cron = "0/50 * 9-12 * * MON-FRI", zone = "Asia/Seoul")
	 public void runFetchCurrencyJob() {
	 	if (jobSucceeded) {
	 		return;
	 	}

	 	try {
	 		log.info("runFetchCurrencyJob 실행됨");
	 		JobExecution execution = jobLauncher.run(fetchCurrencyJob, new JobParametersBuilder()
	 				.addLong("time", System.currentTimeMillis())
	 				.toJobParameters());

	 		log.info("JobExecution: {}", execution);

	 		// 성공하면 플래그 true로 바꿔서 이후 실행 차단
	 		if (execution.getStatus() == BatchStatus.COMPLETED) {
	 			jobSucceeded = true;
	 			log.info("환율 수집 배치 성공! 이후부터 실행 중단됨.");
	 		}

	 	} catch (Exception e) {
	 		log.error("환율 데이터 수집 작업 실행 중 오류 발생", e);
	 	}
	 }
}
