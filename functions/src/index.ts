import express, { Request, Response } from 'express';
import * as admin from 'firebase-admin';
import * as functions from 'firebase-functions';

// Initialize Firebase Admin SDK
admin.initializeApp();

const app = express();
app.use(express.json()); // Middleware to parse JSON

// Define the endpoint to handle Swish callbacks
app.post('/swishCallback', async (req: Request, res: Response) => {
    try {
        const callbackData = req.body;

        // Log the received callback data
        console.log("Swish Callback Data:", callbackData);

        // Save the callback data to a new collection called 'swishCallbacks'
        await admin.firestore().collection('swishCallbacks').add(callbackData);

        // Respond with a success message
        res.status(200).send("Callback received");
    } catch (error) {
        console.error("Error handling Swish callback:", error);
        res.status(500).send("Internal Server Error");
    }
});

// Export the app as a Firebase Cloud Function
export const api = functions.https.onRequest(app);
