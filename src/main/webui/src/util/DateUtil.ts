export const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return `${date.getDay()}-${date.getMonth()}-${date.getFullYear()}`;
};
