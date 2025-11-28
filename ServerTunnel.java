/*  This is free and unencumbered software released into the public domain.

    Anyone is free to copy, modify, publish, use, compile, sell, or
    distribute this software, either in source code form or as a compiled
    binary, for any purpose, commercial or non-commercial, and by any
    means.

    In jurisdictions that recognize copyright laws, the author or authors
    of this software dedicate any and all copyright interest in the
    software to the public domain. We make this dedication for the benefit
    of the public at large and to the detriment of our heirs and
    successors. We intend this dedication to be an overt act of
    relinquishment in perpetuity of all present and future rights to this
    software under copyright law.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
    EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
    IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
    OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
    ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
    OTHER DEALINGS IN THE SOFTWARE.

    For more information, please refer to <http://unlicense.org/>

  or CC0 (see <https://freedomdefined.org/Licenses/CC-0>). */

package downadow.servertunnel;

import com.badlogic.gdx.files.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.net.*;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Input.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ServerTunnel implements ApplicationListener {
    Socket srv, prx;
    BufferedReader ssrv, sprx;
    String srcAddr, dstAddr;
    int srcPort, dstPort;
    
    public void create() {
        String[] data = Gdx.files.local(".servertunnel").readString("UTF-8").replace("\n", "").split(" ");
        srcAddr = data[0].split(":")[0];
        srcPort = Integer.parseInt(data[0].split(":")[1]);
        dstAddr = data[1].split(":")[0];
        dstPort = Integer.parseInt(data[1].split(":")[1]);
        
        srv = Gdx.net.newClientSocket(Protocol.TCP, srcAddr, srcPort, new SocketHints());
        ssrv = new BufferedReader(new InputStreamReader(srv.getInputStream(), StandardCharsets.UTF_8));
        prx = Gdx.net.newClientSocket(Protocol.TCP, dstAddr, dstPort, new SocketHints());
        sprx = new BufferedReader(new InputStreamReader(prx.getInputStream(), StandardCharsets.UTF_8));
        
        new Thread() {
            public void run() {
                try {
                    while(true) srv.getOutputStream().write((sprx.readLine() + "\n").getBytes(StandardCharsets.UTF_8));
                } catch(Exception ex) {}
            }
        }.start();
        
        try {
            while(true) prx.getOutputStream().write((ssrv.readLine() + "\n").getBytes(StandardCharsets.UTF_8));
        } catch(Exception ex) {Gdx.app.log("info", "disconnect");}
    }
    
    public void resize(int width, int height) {}
    public void pause() {}
    public void resume() {}
    public void dispose() {}
    public void render() {}
}
