import { NextResponse } from "next/server";

export async function GET() {
  try {
    return NextResponse.json(
      {
        status: "alive",
        timestamp: new Date().toISOString(),
        uptime: process.uptime(),
      },
      { status: 200 }
    );
  } catch (error) {
    console.error("Health check failed:", error);
    return NextResponse.json(
      {
        status: "dead",
        error: "Application is not responding.",
        timestamp: new Date().toISOString(),
      },
      { status: 503 }
    );
  }
}
