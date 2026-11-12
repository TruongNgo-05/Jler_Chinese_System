export const catalogApi = {
  listPath: (kind) => `/platform/catalog/${kind}`,
  detailPath: (kind, id) => `/platform/catalog/${kind}/${id}`,
};
