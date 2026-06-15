import { describe, it, expect, vi, beforeAll } from 'vitest';

const mockResponseUse = vi.hoisted(() => vi.fn());

vi.mock('axios', () => ({
  default: {
    create: vi.fn(() => ({
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      delete: vi.fn(),
      interceptors: {
        request: { use: vi.fn() },
        response: { use: mockResponseUse },
      },
    })),
  },
}));

// Import client AFTER mocking — triggers interceptor registration on the mock
import '../client';

describe('apiClient interceptor', () => {
  let onFulfilled: (response: any) => any;
  let onRejected: (error: any) => Promise<any>;

  beforeAll(() => {
    const calls = mockResponseUse.mock.calls;
    expect(calls).toHaveLength(1);
    // .use(onFulfilled, onRejected)  — first arg is success, second is error
    onFulfilled = calls[0]![0];
    onRejected = calls[0]![1];
  });

  it('should pass through response when code is 200', () => {
    const response = {
      data: { code: 200, message: 'success', data: {} },
    };
    expect(onFulfilled(response)).toEqual(response);
  });

  it('should reject response when code is not 200', async () => {
    const response = {
      data: { code: 400, message: 'Bad request', data: null },
    };
    await expect(onFulfilled(response)).rejects.toThrow('Bad request');
  });

  it('should reject with default message when code is not 200 and message is empty', async () => {
    const response = {
      data: { code: 500, message: '', data: null },
    };
    await expect(onFulfilled(response)).rejects.toThrow('Request failed');
  });

  it('should reject with default message when code is not 200 and message is undefined', async () => {
    const response = {
      data: { code: 500, data: null },
    };
    await expect(onFulfilled(response)).rejects.toThrow('Request failed');
  });

  it('should pass through network errors via error interceptor', async () => {
    const networkError = new Error('Network Error');
    await expect(onRejected(networkError)).rejects.toThrow('Network Error');
  });
});
