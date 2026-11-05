const canvas = document.getElementById("canvas");
const ctx = canvas.getContext("2d");

const width = canvas.width;
const height = canvas.height;

const centerX = canvas.width / 2;
const centerY = canvas.height / 2;

const scale = 100;
const tickSize = 5;

function draw(rValue) {

    const R_pixels = rValue * scale;
    const half_R_pixels = R_pixels / 2;
    ctx.clearRect(0, 0, width, height);

    ctx.lineWidth = 1.5;

    ctx.beginPath();
    ctx.moveTo(20, centerY);
    ctx.lineTo(width - 20, centerY);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(width - 10, centerY);
    ctx.lineTo(width - 18, centerY - 5);
    ctx.lineTo(width - 18, centerY + 5);
    ctx.closePath();
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(centerX, height - 20);
    ctx.lineTo(centerX, 20);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(centerX, 10);
    ctx.lineTo(centerX - 5, 18);
    ctx.lineTo(centerX + 5, 18);
    ctx.closePath();
    ctx.stroke();

    ctx.beginPath();
    const xTicks = [-R_pixels, -half_R_pixels, half_R_pixels, R_pixels];
    xTicks.forEach(offset => {
        ctx.moveTo(centerX + offset, centerY - tickSize);
        ctx.lineTo(centerX + offset, centerY + tickSize);
    });

    const yTicks = [-R_pixels, -half_R_pixels, half_R_pixels, R_pixels];
    yTicks.forEach(offset => {
        ctx.moveTo(centerX - tickSize, centerY - offset);
        ctx.lineTo(centerX + tickSize, centerY - offset);
    });
    ctx.stroke();

    ctx.fillStyle = "black";

    ctx.fillText("-R", centerX - R_pixels, centerY + 15);
    ctx.fillText("-R/2", centerX - half_R_pixels, centerY + 15);
    ctx.fillText("R/2", centerX + half_R_pixels, centerY + 15);
    ctx.fillText("R", centerX + R_pixels, centerY + 15);
    ctx.fillText("x", width - 12, centerY - 10);

    ctx.fillText("R", centerX + 10, centerY - R_pixels);
    ctx.fillText("R/2", centerX + 10, centerY - half_R_pixels);
    ctx.fillText("-R/2", centerX + 10, centerY + half_R_pixels);
    ctx.fillText("-R", centerX + 10, centerY + R_pixels);
    ctx.fillText("y", centerX + 12, 12);

    ctx.fillStyle = "rgba(51, 153, 255, 0.8)";

    ctx.fillRect(centerX - R_pixels, centerY, R_pixels, half_R_pixels);

    ctx.beginPath();
    ctx.moveTo(centerX, centerY);
    ctx.lineTo(centerX, centerY - R_pixels);
    ctx.lineTo(centerX + R_pixels, centerY);
    ctx.closePath();
    ctx.fill();

    ctx.beginPath();
    ctx.moveTo(centerX, centerY);
    ctx.arc(centerX, centerY, half_R_pixels, Math.PI, 1.5 * Math.PI, false);
    ctx.closePath();
    ctx.fill();
}

function drawPoint(x,y,r,isHit) {
    const PixelX = centerX + (x * scale)
    const PixelY = centerY - (y * scale)

    ctx.beginPath();

    ctx.arc(PixelX, PixelY, 6, 0, 2 * Math.PI, false);

    ctx.fillStyle = isHit ? "#08ea08" : "#f10707";
    ctx.fill();
    ctx.lineWidth = 1;
    ctx.strokeStyle = "black";
    ctx.lineWidth = 2;
    ctx.stroke();
}



