const getFile = (blob: Blob, filename: string) => {
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement("a");

  link.href = url;
  link.download = filename;

  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);

  window.URL.revokeObjectURL(url);
};

export const downloadFile = async (response: Response, filename: string) => {
  const contentDisposition = response.headers.get("Content-Disposition");

  if (contentDisposition) {
    const matches = contentDisposition.match(/filename="([^"]+)"/);
    if (matches && matches[1]) {
      filename = matches[1];
    }
  }

  const blob = await response.blob();

  getFile(blob, filename);
};

export const downloadBlob = (blob: Blob, filename: string) => {
  getFile(blob, filename);
};
