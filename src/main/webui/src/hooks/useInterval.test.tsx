import { renderHook } from "@testing-library/react";
import useInterval from "./useInterval";

describe("useInterval", () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("calls callback at each interval tick", () => {
    const callback = vi.fn();

    renderHook(() => useInterval(callback, 1000));

    vi.advanceTimersByTime(3000);

    expect(callback).toHaveBeenCalledTimes(3);
  });

  it("does not call callback before first interval elapses", () => {
    const callback = vi.fn();

    renderHook(() => useInterval(callback, 1000));

    vi.advanceTimersByTime(999);

    expect(callback).not.toHaveBeenCalled();
  });

  it("uses latest callback reference", () => {
    const firstCallback = vi.fn();
    const secondCallback = vi.fn();

    const { rerender } = renderHook(
      ({ callback }) => useInterval(callback, 1000),
      { initialProps: { callback: firstCallback } },
    );

    vi.advanceTimersByTime(1000);
    expect(firstCallback).toHaveBeenCalledTimes(1);

    rerender({ callback: secondCallback });

    vi.advanceTimersByTime(1000);
    expect(secondCallback).toHaveBeenCalledTimes(1);
  });

  it("cleans up interval on unmount", () => {
    const callback = vi.fn();

    const { unmount } = renderHook(() => useInterval(callback, 1000));

    vi.advanceTimersByTime(1000);
    expect(callback).toHaveBeenCalledTimes(1);

    unmount();

    vi.advanceTimersByTime(3000);
    expect(callback).toHaveBeenCalledTimes(1);
  });
});
