package com.example.SecureAndBox.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

import com.example.SecureAndBox.dto.CodeSubmission;
import com.example.SecureAndBox.etc.LanguageType;
import com.example.SecureAndBox.service.ProblemService;
import com.example.SecureAndBox.service.SecureCodeService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/problem")
@RequiredArgsConstructor
public class ProblemController {

	private final SecureCodeService secureCodeService;

	private final ProblemService problemService;



	private String processCodeSubmission(Long problemId, String languageType, String userCode) {
		// Simulate code execution or verification and return result
		return "Processed code for problem ID " + problemId;
	}

	@Async
	@PostMapping("/submit")
	public CompletableFuture<ResponseEntity<String>> handleFileUpload(@RequestBody CodeSubmission submission) {


		return secureCodeService.verifyAndForwardCode(submission)
			.thenApply(response -> ResponseEntity.ok(response))
			.exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("An error occurred: " + ex.getMessage()));
	}


	@GetMapping("")
	public ResponseEntity<?> getProblemList(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Pageable pageable = PageRequest.of(page, size);

		return ResponseEntity.ok(problemService.getProblemList(pageable));
	}

	@GetMapping("/topic")
	public ResponseEntity<?> getProblemListByTopic(@RequestParam(defaultValue="login") String topic)
	 {
		return ResponseEntity.ok(problemService.getProblemListByTopic(topic));
	}

	@GetMapping("/difficulty")
	public ResponseEntity<?> getProblemListByDfficulty(@RequestParam(defaultValue="EASY") String difficulty)
	{
		return ResponseEntity.ok(problemService.getProblemListByDifficulty(difficulty));
	}
	@Operation(summary="스켈레톤 코드 가져오기")
	@GetMapping("/skeleton-code")
	public ResponseEntity<String> getSkeletonCode(@RequestParam("filename") String filename,@RequestParam LanguageType type) {
		try {
			String content = problemService.getSkeletonCode(filename,type);
			return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, "text/plain;charset=UTF-8")
				.body(content);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File not found or read error");
		}
	}
	@GetMapping("/details")
	public ResponseEntity<?> getProblemDetails(
		@RequestParam Long problemId,
		@RequestParam(defaultValue = "JAVA") LanguageType type) throws IOException {
		return ResponseEntity.ok(problemService.getProblem(problemId,type));
	}
}