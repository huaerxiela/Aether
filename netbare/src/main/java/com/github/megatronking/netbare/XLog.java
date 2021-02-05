/*  NetBare - An android network capture and injection library.
 *  Copyright (C) 2018-2019 Megatron King
 *  Copyright (C) 2018-2019 GuoShi
 *
 *  NetBare is free software: you can redistribute it and/or modify it under the terms
 *  of the GNU General Public License as published by the Free Software Found-
 *  ation, either version 3 of the License, or (at your option) any later version.
 *
 *  NetBare is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 *  PURPOSE. See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with NetBare.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.megatronking.netbare;

import com.github.megatronking.netbare.net.Session;
import com.github.megatronking.netbare.ip.Protocol;
import com.sanbo.utils.EL;

/**
 * A log util using in NetBare, it uses the protocol, ip and port as the prefix.
 *
 * @author Megatron King
 * @since 2018-10-14 10:25
 */
public final class XLog {

    private final String mPrefix;

    /**
     * Constructs a NetBareXLog instance with the net information.
     *
     * @param protocol The IP protocol.
     * @param ip The ip address, a string value.
     * @param port The port, a short value.
     */
    public XLog(Protocol protocol, String ip, short port) {
        this(protocol, ip, NetBareUtils.convertPort(port));
    }

    /**
     * Constructs a NetBareXLog instance with the net information.
     *
     * @param protocol The IP protocol.
     * @param ip The ip address, a int value.
     * @param port The port, a short value.
     */
    public XLog(Protocol protocol, int ip, short port) {
        this(protocol, NetBareUtils.convertIp(ip), port);
    }

    /**
     * Constructs a NetBareXLog instance with the net information.
     *
     * @param protocol The IP protocol.
     * @param ip The ip address, a int value.
     * @param port The port, a int value.
     */
    public XLog(Protocol protocol, int ip, int port) {
        this(protocol, NetBareUtils.convertIp(ip), port);
    }

    /**
     * Constructs a NetBareXLog instance with the net information.
     *
     * @param session The session contains net information.
     */
    public XLog(Session session) {
        this(session.protocol, session.remoteIp, session.remotePort);
    }

    /**
     * Constructs a NetBareXLog instance with the net information.
     *
     * @param protocol The IP protocol.
     * @param ip The ip address, a string value.
     * @param port The port, a int value.
     */
    public XLog(Protocol protocol, String ip, int port) {
        this.mPrefix = "[" + protocol.name() + "][" + ip + ":" + port + "]";
    }

    /**
     * Print a verbose level log in console, format is '[protocol][ip:port]message'.
     *
     * @param msg The message you would like logged.
     */
    public void v(String msg) {
        EL.v(mPrefix + msg);
    }


    public void v(String msg, Object... args) {
        EL.v(mPrefix + msg, args);
    }

    /**
     * Print a debug level log in console, format is '[protocol][ip:port]message'.
     *
     * @param msg The message you would like logged.
     */
    public void d(String msg) {
        EL.d(mPrefix + msg);
    }

    /**
     * Print a verbose level log in console, format is '[protocol][ip:port]message'.
     *
     * @param msg The message you would like logged.
     * @param args Arguments referenced by the format specifiers in the format string.
     */
    public void d(String msg, Object... args) {
        EL.d(mPrefix + msg, args);
    }

    /**
     * Print a info level log in console, format is '[protocol][ip:port]message'.
     *
     * @param msg The message you would like logged.
     */
    public void i(String msg) {
        EL.i(mPrefix + msg);
    }

    /**
     * Print a info level log in console, format is '[protocol][ip:port]message'.
     *
     * @param msg The message you would like logged.
     * @param args Arguments referenced by the format specifiers in the format string.
     */
    public void i(String msg, Object... args) {
        EL.i(mPrefix + msg, args);
    }

    /**
     * Print a error level log in console, format is '[protocol][ip:port]message'.
     *
     * @param msg The message you would like logged.
     */
    public void e(String msg) {
        EL.e(mPrefix + msg);
    }

    /**
     * Print a error level log in console, format is '[protocol][ip:port]message'.
     *
     * @param msg The message you would like logged.
     * @param args Arguments referenced by the format specifiers in the format string.
     */
    public void e(String msg, Object... args) {
        EL.e(mPrefix + msg, args);
    }

    /**
     * Print a warning level log in console, format is '[protocol][ip:port]message'.
     *
     * @param msg The message you would like logged.
     */
    public void w(String msg) {
        EL.w(mPrefix + msg);
    }

    /**
     * Print a warning level log in console, format is '[protocol][ip:port]message'.
     *
     * @param msg The message you would like logged.
     * @param args Arguments referenced by the format specifiers in the format string.
     */
    public void w(String msg, Object... args) {
        EL.w(mPrefix + msg, args);
    }

}
