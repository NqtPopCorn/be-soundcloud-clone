package com.popcorn.soundcloudclone;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class SoundCloudCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoundCloudCloneApplication.class, args);

		// tao user ban dau

	}

}
