package com.thesis.mtbalance;

/**
 * Helper class for 3D vector math.
 */
public class VecHelper {

    public VecHelper() {
    }

    /**
     * Calculates the yaw correction matrix.
     * The matrix gives all the sensors the same local frame by correcting for changing yaw.
     *
     * @param quat - the quaternion carrying the yaw correction value.
     * @return the yaw correction matrix used for rotation correction.
     */
    public float[][] yawCorrectionMatrix(float[] quat) {
        // Calculate the yaw of the quaternion (in radians)
        // https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
        double yaw = -Math.atan2(2d * (quat[0] * quat[3] + quat[1] * quat[2]),
                1d - 2d * (quat[2] * quat[2] + quat[3] * quat[3]));

        // Pre calculate the cosine and sine of the yaw
        float cYaw = (float) Math.cos(yaw);
        float sYaw = (float) Math.sin(yaw);

        // Calculate each row of the matrix
        float[] xRot = {cYaw, -sYaw, 0f};
        float[] yRot = {sYaw, cYaw, 0f};
        float[] zRot = {0f, 0f, 1f};

        // Return the rotation matrix
        return new float[][]{xRot, yRot, zRot};
    }

    /**
     * Rotates a 3D vector given a quaternion.
     *
     * @param yawCorrMatrix - matrix used to correct the rotation to the current frame.
     * @param quat          - the quaternion to rotate with.
     * @param dimension     - the length of the vector to rotate (rotation around point).
     */
    public float[] quatRotation(float[][] yawCorrMatrix, float[] quat, float dimension) {
        // Make a 4-dimensional point and the conjugate needed for rotation
        float[] point = {0f, dimension, 0f, 0f};
        float[] conj = {quat[0], -quat[1], -quat[2], -quat[3]};

        // Calculate the result
        float[] result = hamiltonProduct(hamiltonProduct(quat, point), conj);

        // Return the xyz result after correction by the yaw matrix
        return yawCorrection(yawCorrMatrix, new float[]{result[1], result[2], result[3]});
    }

    /**
     * Returns the hamilton product of two 4-dimensional vectors.
     *
     * @param p - the first vector.
     * @param q - the second vector.
     * @return the product vector of the hamilton product.
     */
    private float[] hamiltonProduct(float[] p, float[] q) {
        // Returns the Hamilton product - https://en.wikipedia.org/wiki/Quaternion#Hamilton_product
        return new float[]
                       {p[0] * q[0] - p[1] * q[1] - p[2] * q[2] - p[3] * q[3],
                        p[0] * q[1] + p[1] * q[0] + p[2] * q[3] - p[3] * q[2],  // i
                        p[0] * q[2] - p[1] * q[3] + p[2] * q[0] + p[3] * q[1],  // j
                        p[0] * q[3] + p[1] * q[2] - p[2] * q[1] + p[3] * q[0]}; // k
    }

    /**
     * Corrects the vector according to the yaw correction matrix.
     * Ensures that all sensors are in the same local frame, avoiding axes flipping due to yaw.
     *
     * @param yawCorrMatrix - the matrix to correct the vector by.
     * @param vec           - the vector to correct.
     * @return a corrected vector.
     */
    private float[] yawCorrection(float[][] yawCorrMatrix, float[] vec) {
        return new float[]{
                dot(yawCorrMatrix[0], vec),
                dot(yawCorrMatrix[1], vec),
                dot(yawCorrMatrix[2], vec)
        };
    }

    /**
     * Gets the offset of the crank depending on the current bike pitch.
     *
     * @param quat         - the quat containing the pitch information.
     * @param sensorOffset - the sensor offset in a base position.
     * @return a position manipulated by the pitch for more precise readings.
     */
    public float[] getOffsetPosition(float[] quat, float[] sensorOffset) {
        // Calculate the sine pitch of the quaternion (in radians)
        // https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
        double sPitch = 2d * (quat[0] * quat[2] - quat[3] * quat[1]);

        // Avoid gimbal lock when pitch approaches ~90 degrees and calculate the cosine pitch
        double pitch = (Math.abs(sPitch) >= 1) ?
                Math.copySign(Math.PI / 2d, sPitch) : Math.asin(sPitch);
        double cPitch = Math.cos(pitch);

        // Calculate the new x and y positions of the initial offset and return it.
        double offsetY = sensorOffset[1] * cPitch - sensorOffset[2] * sPitch;
        double offsetZ = sensorOffset[1] * sPitch + sensorOffset[2] * cPitch;
        return new float[]{0f, (float) offsetY, (float) offsetZ};
    }

    /**
     * Mirrors a vector in the z-axis, optionally correcting the slope.
     *
     * @param vec        - the vector to mirror.
     * @param correction - whether to correct the slope or not.
     * @param magnitude  - the magnitude of the correction.
     * @return a mirrored, possibly corrected vector.
     */
    public float[] mirrorVector(float[] vec, boolean correction, float magnitude) {
        // Correct the x- and y-axes with the magnitude if correction is enabled
        if (correction) {
            vec[0] *= magnitude;
            vec[1] *= magnitude;
        }

        // Invert the x- and y-axes to mirror the vector through the z-axis
        vec[0] = -vec[0];
        vec[1] = -vec[1];

        return vec;
    }

    /**
     * Returns the current balance (end effector) given supplied vectors.
     *
     * @param offsetVec - the offset between the bike sensor and crank.
     * @param pedalVec  - the current pedal vector.
     * @param ankleVec  - the current ankle vector.
     * @param kneeVec   - the current knee vector.
     * @param hipOffset - the offset from the hip to the middle of the pelvis.
     * @return the end effector built from the other vectors.
     */
    public float[] getEndEffector(float[] offsetVec, float[] pedalVec,
                                  float[] ankleVec, float[] kneeVec, float[] hipOffset) {
        // Create a new float array and pairwise add all the other vectors
        float[] endEffector = new float[3];
        for (int i = 0; i < 3; i++)
            endEffector[i] = offsetVec[i] + pedalVec[i] + ankleVec[i] + kneeVec[i] + hipOffset[i];

        return endEffector;
    }

    /**
     * Get the closest intersection point between a line and a point.
     *
     * @param p2 - point denoting end of line segment.
     * @param q  - separate point to draw intersection point from.
     * @return the closest intersection point on line (p1, p2).
     */
    public float[] getIntersection(float[] p2, float[] q) {
        // The start of the line segment is always the origin
        float[] p1 = {0f, 0f, 0f};

        // Get the inner product space (u) and the line between the segment and point
        float[] u = sub(p2, p1);
        float[] pq = sub(q, p1);

        // Get the point on the line nearest q to get the component on the line
        float[] w = sub(pq, mul(u, dot(pq, u) / norm(u)));

        // Sub w from q to get the actual point on the line
        return sub(q, w);
    }

    /**
     * Get the balance difference between two points.
     *
     * @param q1 - the offset point.
     * @param q2 - the origin point.
     * @return A 2-dimensional balance difference with q2 as origin.
     */
    public float[] getBalanceDifference(float[] q1, float[] q2) {
        // Subtract the vectors and flatten the result
        float[] result = sub(q1, q2);
        return new float[]{result[0], result[1]};
    }

    /**
     * Get the distance between two points.
     *
     * @param q1 - the first point.
     * @param q2 - the second point.
     * @return the distance with float precision.
     */
    public float getDistance(float[] q1, float[] q2) {
        // Use the Pythagorean theorem to get the distance between two points
        float[] d = sub(q1, q2);
        return (float) Math.sqrt(d[0] * d[0] + d[1] * d[1] + d[2] * d[2]);
    }

    /**
     * Returns the angle between two vectors.
     *
     * @param p - the first vector.
     * @param q - the second vector.
     * @return the angle between the vectors in degrees.
     */
    public float getAngle(float[] p, float[] q) {
        float dot = dot(p, q);
        float length = length(p) * length(q);

        double radians = Math.acos(dot / length);
        return (float) Math.toDegrees(radians);
    }

    // region Elemental

    /**
     * Subtracts vector q from vector p.
     *
     * @param p - first vector.
     * @param q - second vector.
     * @return subtracted vector.
     */
    private float[] sub(float[] p, float[] q) {
        return new float[]{p[0] - q[0], p[1] - q[1], p[2] - q[2]};
    }

    /**
     * Multiplies a vector with a scalar.
     *
     * @param p - the vector.
     * @param s - the scalar.
     * @return a vector multiplied by a scalar.
     */
    private float[] mul(float[] p, float s) {
        return new float[]{p[0] * s, p[1] * s, p[2] * s};
    }

    /**
     * Returns the dot product of two vectors.
     *
     * @param p - the first vector.
     * @param q - the second vector.
     * @return the dot product of the two vectors.
     */
    private float dot(float[] p, float[] q) {
        return p[0] * q[0] + p[1] * q[1] + p[2] * q[2];
    }

    /**
     * Returns the norm of a vector (also known as its squared length).
     *
     * @param p - the vector.
     * @return the norm of the vector.
     */
    private float norm(float[] p) {
        return p[0] * p[0] + p[1] * p[1] + p[2] * p[2];
    }

    /**
     * Returns the length of a vector.
     *
     * @param p - the vector.
     * @return the length of the vector.
     */
    private float length(float[] p) {
        return (float) Math.sqrt(norm(p));
    }
    // endregion
}
