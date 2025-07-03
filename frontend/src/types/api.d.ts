export type SuccessResponse<T> = {
  data: T;
  status: string;
  message: string;
};

export type ErrorResponse = {
  status: number;
  divisionCode: string;
  resultMsg: string;
  errors: string[];
  reason: string;
};
