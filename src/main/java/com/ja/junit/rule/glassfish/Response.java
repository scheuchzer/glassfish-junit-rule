package com.ja.junit.rule.glassfish;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

@RequiredArgsConstructor
@Getter
@Slf4j
public class Response {

	private final int status;
	private final byte[] content;
	private final String encoding;

	public String getContentAsString() {
		try {
			return new String(content, encoding);
		} catch (UnsupportedEncodingException e) {
			log.error("Unable to convert to string", e);
			fail();
		}
		return null;
	}

	public static Response create(int statusCode, @NonNull HttpEntity entity) {
		try {
			return new Response(statusCode, EntityUtils.toByteArray(entity),
					(entity.getContentEncoding() == null ? "UTF-8" : entity
							.getContentEncoding().getValue()));
		} catch (IOException e) {
			log.error("Problems with entity.", e);
			fail();
		}
		return null;

	}
}
