package io.egargo.jwt;

public class ApiResponse<T> {
	public T data;
	public String message;

	public ApiResponse() {
	}

	public ApiResponse<T> setData(T data) {
		this.data = data;
		return this;
	}

	public ApiResponse<T> setMessage(String message) {
		this.message = message;
		return this;
	}
}
