


function checkHit(x,y,r) {
    const halfR = r / 2;

    if (x >= 0 && y >= 0 ) {
        return (x + y )<= r;
    }

    if (x <= 0 && y >= 0 ) {

        return (x * x + y * y) <= (halfR * halfR);
    }

    if (x <=0 && y <= 0){
        return (x >= -r) && (x <= -halfR) && (y >= -halfR) && (y <= 0);
    }

    return false;

}

async function processPointAsync(x,y,r) {
    await new Promise(resolve => setTimeout(resolve,300));

    const hitResult = checkHit(x,y,r);
    const now  = new Date().toISOString();

    return{
        x: x,
        y: y,
        r: r,
        hit: hitResult,
        timestamp: now
    };
}