import { NextResponse } from "next/server";

const MEMORY_USAGE_THRESHOLD_PERCENT = 95;

type CheckResult = {
  status: "ok" | "warning";
  message: string;
};

export async function GET() {
  try {
    const checks = {
      memory: checkMemoryUsage(),
    };

    const isReady = Object.values(checks).every(
      (check) => check.status === "ok"
    );

    if (isReady) {
      return NextResponse.json(
        {
          status: "ready",
          timestamp: new Date().toISOString(),
          checks,
        },
        { status: 200 }
      );
    } else {
      return NextResponse.json(
        {
          status: "not_ready",
          timestamp: new Date().toISOString(),
          checks,
        },
        { status: 503 }
      );
    }
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

function checkMemoryUsage(): CheckResult {
  const memUsage = process.memoryUsage();
  const memUsagePercent =
    memUsage.heapTotal > 0 ? (memUsage.heapUsed / memUsage.heapTotal) * 100 : 0;

  if (memUsagePercent > MEMORY_USAGE_THRESHOLD_PERCENT) {
    return {
      status: "warning",
      message: `High memory usage: ${memUsagePercent.toFixed(2)}%`,
    };
  }

  return {
    status: "ok",
    message: `Memory usage: ${memUsagePercent.toFixed(2)}%`,
  };
}
