
let selectedX = null;
document.addEventListener("DOMContentLoaded", () => {
    const selectR = document.getElementById("R");
    const yInput = document.getElementById("y-input");
    const errorBox = document.getElementById("error-box");
    const form = document.getElementById("form_id");
    const resultBody = document.getElementById("result_body");
    const submitButton = document.getElementById("submit_btn");
    const BACKEND_URL = "/fcgi-bin/hello-world.jar";

    function showError(message) {
        errorBox.textContent = message;
        errorBox.style.display = "block";
    }

    function hideError() {
        errorBox.textContent = "";
        errorBox.style.display = "none";
    }

    function validateX() {
        if (selectedX === null) {
            showError("Не выбрано значение X");
            return false;
        }

        return true;
    }

    function validateY() {
        const rawValue = yInput.value.trim().replace(",", ".");

        if (rawValue === "") {
            showError("Поле Y не должно быть пустым");
            return false;
        }

        const value = Number(rawValue);

        if (!Number.isFinite(value)) {
            showError("Y должен быть числом");
            return false;
        }

        if (value <= -5 || value >= 3) {
            showError("Y должен быть в диапазоне от -5 до 3 (не включая границы)");
            return false;
        }
        return value;
    }

    function validateR() {
        const value = Number(selectR.value);
        const validValues = [1, 1.5, 2, 2.5, 3];

        if (!validValues.includes(value)) {
            showError("R должен быть равен одному из значений: 1, 1.5, 2, 2.5, 3");
            return false;
        }

        return value;
    }

    const buttonList = document.querySelector(".buttons_list_X");
    buttonList.addEventListener("click", (event) => {
        const button = event.target.closest(".x-btn");

        if (!button) {
            return;
        }
        selectedX = Number(button.value);
        document.querySelectorAll(".x-btn").forEach((item) => {
            item.classList.remove("selected");
        });

        button.classList.add("selected");

        hideError();
    });

    draw(Number(selectR.value));

    selectR.addEventListener("change", () => {
        draw(Number(selectR.value));
    });

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        hideError();

        const xValid = validateX();
        const yValue = validateY();
        const rValue  = validateR();

        if (!xValid || yValue === false || rValue === false) {
            return;
        }

        const data = new URLSearchParams();
        data.append("x", String(selectedX));
        data.append("y", String(yValue));
        data.append("r", String(rValue));

        submitButton.disabled = true;

        try {
            const response = await fetch(BACKEND_URL, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: data
            });

            const responseText = await response.text();

            if (!response.ok) {
                throw new Error(`Ошибка сервера (${response.status}): ${responseText}`);
            }

            let results;

            try {
                results = JSON.parse(responseText);
            } catch {
                throw new Error("Сервер вернул некорректный JSON");
            }

            if (!Array.isArray(results)) {
                throw new Error("Ожидался массив результатов");
            }

            resultBody.innerHTML = "";

            results.forEach((item) => {
                addRowToTable(item);
            });

            draw(rValue);

            results.forEach((item) => {
                drawPoint(Number(item.x), Number(item.y), Number(item.r), item.hit);
            });

        } catch (error) {
            console.error(error);
            showError(error.message || "Не удалось связаться с сервером");
        } finally {
            submitButton.disabled = false;
        }
    });

    function addRowToTable(item) {
        const tr = document.createElement("tr");

        const date = new Date(Number(item.timestamp));

        const formattedDate = new Intl.DateTimeFormat("ru-RU", {
            dateStyle: "short",
            timeStyle: "medium"
        }).format(date);

        const executionTime = Number(item.executionTime).toFixed(3);
        const hitText = item.hit ? "Попал" : "Мимо";

        tr.innerHTML = `
        <td>${item.x}</td>
        <td>${item.y}</td>
        <td>${item.r}</td>
        <td>${formattedDate}</td>
        <td>${hitText}</td>
        <td>${executionTime}</td>
    `;

        resultBody.appendChild(tr);
    }
});
