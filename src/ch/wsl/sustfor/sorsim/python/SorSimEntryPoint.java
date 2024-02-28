package ch.wsl.sustfor.sorsim.python;

import ch.wsl.sustfor.sorsim.wrapper.SimpleWrapper;

import py4j.GatewayServer;

/**
 * 
 * @author Stefan Holm
 *
 */
public class SorSimEntryPoint {

	public static void main(String[] args) {
        GatewayServer gatewayServer = new GatewayServer(new SorSimEntryPoint());
        gatewayServer.start();
        System.out.println("SorSim Gateway Server (Py4J) Started");
	}
	
	public SimpleWrapper getSimpleWrapper() {
		return new SimpleWrapper();
	}
}
