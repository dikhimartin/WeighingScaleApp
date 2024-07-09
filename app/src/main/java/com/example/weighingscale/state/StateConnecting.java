package com.example.weighingscale.state;

/**
 * StateConnecting class to handle Bluetooth connection statuses.
 *
 * Usage:
 * - Set the status: stateConnecting.setStatus(StateConnecting.BLUETOOTH_DISABLED);
 * - Get the current status: int currentStatus = stateConnecting.getStatus();
 *
 * State mapping:
 * - StateConnecting.BLUETOOTH_DISABLED = 0 // Bluetooth is disabled
 * - StateConnecting.BLUETOOTH_ENABLED = 1  // Bluetooth is enabled
 * - StateConnecting.BLUETOOTH_CONNECTED = 2 // Bluetooth is enabled and a device is connected
 */
public class StateConnecting {

    public static final int BLUETOOTH_DISABLED = 0;
    public static final int BLUETOOTH_ENABLED = 1;
    public static final int BLUETOOTH_CONNECTED = 2;

    private int currentState;

    public StateConnecting() {
        // Initialize to a default state, for example, Bluetooth disabled
        this.currentState = BLUETOOTH_DISABLED;
    }

    /**
     * Set the current status
     * @param status The new status to be set
     */
    public void setStatus(int status) {
        this.currentState = status;
    }

    /**
     * Get the current status
     * @return The current status
     */
    public int getStatus() {
        return this.currentState;
    }
}
