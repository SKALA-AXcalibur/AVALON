import { NextResponse } from "next/server";

export async function GET() {
  try {
    return NextResponse.json(
      {
        status: "ready",
        timestamp: new Date().toISOString(),
      },
      { status: 200 }
    );
  } catch (error) {
    console.error("Readiness check failed:", error);
    return NextResponse.json(
      {
        status: "not_ready",
        error: "Application is not ready to serve requests",
        timestamp: new Date().toISOString(),
      },
      { status: 503 }
    );
  }
}
