export const fetchApi = async (path: string)=> {
    return fetch(path, {
        headers: [["X-Requested-With", "JavaScript"]],
    }).then((response) => {
        if (response.status === 499 && response.headers.has("www-authenticate")) {
            localStorage.setItem("tapir", "login");
            window.addEventListener("storage", () => {
                if (!localStorage.getItem("tapir")) {
                    window.location.reload();
                }
            });
            window.open("/login/ui", "popup", "width=600,height=600");
            return;
        }
        return response.json();
    });
}