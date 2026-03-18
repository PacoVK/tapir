import { renderHook, act } from "@testing-library/react";
import useDebounce from "./useDebounce";

describe("useDebounce", () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("returns initial value immediately on first render", () => {
    const { result } = renderHook(() => useDebounce("initial", 500));
    expect(result.current).toBe("initial");
  });

  it("returns updated value after delay elapses", () => {
    const { result, rerender } = renderHook(
      ({ value, delay }) => useDebounce(value, delay),
      { initialProps: { value: "initial", delay: 500 } },
    );

    rerender({ value: "updated", delay: 500 });

    act(() => {
      vi.advanceTimersByTime(500);
    });

    expect(result.current).toBe("updated");
  });

  it("does not update value before delay elapses", () => {
    const { result, rerender } = renderHook(
      ({ value, delay }) => useDebounce(value, delay),
      { initialProps: { value: "initial", delay: 500 } },
    );

    rerender({ value: "updated", delay: 500 });

    act(() => {
      vi.advanceTimersByTime(300);
    });

    expect(result.current).toBe("initial");
  });

  it("resets timer when value changes before delay completes", () => {
    const { result, rerender } = renderHook(
      ({ value, delay }) => useDebounce(value, delay),
      { initialProps: { value: "first", delay: 500 } },
    );

    rerender({ value: "second", delay: 500 });

    act(() => {
      vi.advanceTimersByTime(300);
    });

    rerender({ value: "third", delay: 500 });

    act(() => {
      vi.advanceTimersByTime(300);
    });

    // Only 300ms since last change, so still showing initial value
    expect(result.current).toBe("first");

    act(() => {
      vi.advanceTimersByTime(200);
    });

    // Now 500ms since last change ("third"), it should update to final value
    expect(result.current).toBe("third");
  });
});
