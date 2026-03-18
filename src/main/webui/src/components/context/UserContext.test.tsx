import { renderHook, act } from "@testing-library/react";
import { UserProvider, useUserContext } from "./UserContext";
import { createUser, createAdminUser } from "../../test/mocks/fixtures";

describe("UserContext", () => {
  it("provides user object to children via useUserContext", () => {
    const user = createUser();
    const { result } = renderHook(() => useUserContext(), {
      wrapper: ({ children }) => (
        <UserProvider fetchedUser={user}>{children}</UserProvider>
      ),
    });

    expect(result.current.user).toEqual(user);
  });

  it("sets isAdmin to true when user roles include 'admin'", () => {
    const adminUser = createAdminUser();
    const { result } = renderHook(() => useUserContext(), {
      wrapper: ({ children }) => (
        <UserProvider fetchedUser={adminUser}>{children}</UserProvider>
      ),
    });

    expect(result.current.isAdmin).toBe(true);
  });

  it("sets isAdmin to false when user roles do not include 'admin'", () => {
    const user = createUser();
    const { result } = renderHook(() => useUserContext(), {
      wrapper: ({ children }) => (
        <UserProvider fetchedUser={user}>{children}</UserProvider>
      ),
    });

    expect(result.current.isAdmin).toBe(false);
  });

  it("calls fetch at 240-second intervals", () => {
    vi.useFakeTimers();
    const fetchSpy = vi.spyOn(global, "fetch");
    const user = createUser();

    renderHook(() => useUserContext(), {
      wrapper: ({ children }) => (
        <UserProvider fetchedUser={user}>{children}</UserProvider>
      ),
    });

    const initialCallCount = fetchSpy.mock.calls.filter((call) =>
      String(call[0]).includes("tapir/user"),
    ).length;

    act(() => {
      vi.advanceTimersByTime(240000);
    });

    const afterOneIntervalCount = fetchSpy.mock.calls.filter((call) =>
      String(call[0]).includes("tapir/user"),
    ).length;

    expect(afterOneIntervalCount).toBeGreaterThan(initialCallCount);

    act(() => {
      vi.advanceTimersByTime(240000);
    });

    const afterTwoIntervalsCount = fetchSpy.mock.calls.filter((call) =>
      String(call[0]).includes("tapir/user"),
    ).length;

    expect(afterTwoIntervalsCount).toBeGreaterThan(afterOneIntervalCount);

    fetchSpy.mockRestore();
    vi.useRealTimers();
  });
});
