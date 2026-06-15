export interface BaseResponse<T = unknown> {
  code: number;
  message: string;
  data: T;
}

export interface PageData<T> {
  total: number;
  pages: number;
  pageNum: number;
  pageSize: number;
  list: T[];
}

export interface BasePageRequest {
  pageNum: number;
  pageSize: number;
}
