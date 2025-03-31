document.getElementById('java-image').onclick = (event) => {
    const numDigits = Math.floor(Math.random() * 20) + 30; // 30-50 particles
    const colors = ['lightskyblue', 'hotpink', 'gold', 'springgreen']; // Color options
    const color = colors[Math.floor(Math.random() * colors.length)]

    for (let i = 0; i < numDigits; i++) {
        const digit = document.createElement('div');
        digit.textContent = Math.random() > 0.5 ? '1' : '0'; // Random 1 or 0
        digit.className = 'digit';

        let x = event.pageX;
        let y = event.pageY;

        // Set initial styles
        digit.style.left = `${x}px`;
        digit.style.top = `${y}px`;
        digit.style.color = color;
        document.body.appendChild(digit);

        // Randomize motion
        const angle = Math.random() * 2 * Math.PI;
        const speed = Math.random() * 3 + 6; // Initial speed
        const gravity = 0.3;  // Acceleration due to gravity
        const vx = Math.cos(angle) * speed;  // horizontal velocity
        let vy = Math.sin(angle) * speed;  // vertical velocity
        let opacity = 1;

        const animate = () => {
            x += vx;
            y += vy;

            vy += gravity;

            opacity -= 0.03;  // Fade out over time

            digit.style.top = `${y}px`;
            digit.style.left = `${x}px`;
            digit.style.opacity = opacity;

            if ( opacity > 0 ) {
                requestAnimationFrame(animate);
            } else {
                digit.remove();
            }
        }

        requestAnimationFrame(animate);
    }
}