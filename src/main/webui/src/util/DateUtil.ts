export const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return formatSimpleDate(date);
};

export const formatDateTime = (dateString: string) => {
  const date = new Date(dateString);
  return `${formatSimpleDate(date)} ${padToString(
    date.getHours(),
  )}:${padToString(date.getMinutes())}`;
};

const padToString = (number: number) => String(number).padStart(2, "0");

const formatSimpleDate = (date: Date) => {
  return `${padToString(date.getDate())}-${padToString(
    date.getMonth() + 1,
  )}-${date.getFullYear()}`;
};
