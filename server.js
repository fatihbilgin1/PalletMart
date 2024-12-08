const express = require('express');
const app = express();
const cors = require('cors');
const Stripe = require('stripe');
const path = require('path');  // 'path' modülünü dahil edin

const stripe = Stripe('sk_test_51QSkxiRwakwQxGFivBYxzIlqWeaEyX7nBW2cS3Wd18zliglihY0GyFnK5Ya9ID1zDHQgQ5XNB7kaKQMEhVvMOe5B00s6yvM3is');

app.use(cors());
app.use(express.json());


app.use(express.static(path.join(__dirname, 'public')));


app.post('/create-payment-intent', async (req, res) => {
    const { amount, currency } = req.body;

    try {
        // Ödeme simülasyonu
        const paymentIntent = await stripe.paymentIntents.create({
            amount: amount,
            currency: currency || 'usd',
            automatic_payment_methods: {
                enabled: true,
            },
        });

        res.status(200).json({ clientSecret: paymentIntent.client_secret });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});
app.get('/cart', (req, res) => {
    res.sendFile('path_to_cart_page.html'); // Sepet sayfasına yönlendir
});

app.listen(3000, '0.0.0.0', () => {
    console.log('Sunucu 3000 portunda çalışıyor.');
});