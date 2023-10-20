import { formatDate, formatDateTime } from "./DateUtil";

describe("DateUtil", () => {
  const dateString = "2023-01-01T15:11:54.000Z";

  it("should format date and time with trailing zeros", () => {
    const dateString = "2023-01-01T01:01:54.000Z";
    const formattedDateTime = formatDateTime(dateString);
    expect(formattedDateTime).toStrictEqual("01-01-2023 02:01");
  });

  it("should format date", () => {
    const formattedDate = formatDate(dateString);
    expect(formattedDate).toStrictEqual("01-01-2023");
  });

  it("should format date and time", () => {
    const formattedDateTime = formatDateTime(dateString);
    expect(formattedDateTime).toStrictEqual("01-01-2023 16:11");
  });
});
