package br.gov.sp.fatec.ubsandroidapp;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.Map;

public class NsdClient {

    private static final String TAG = "NsdClient";
    private final NsdManager nsdManager;
    private final String serviceType;
    private NsdManager.DiscoveryListener discoveryListener;
    private NsdManager.ResolveListener resolveListener;
    private final ServiceDiscoveryListener listener;

    public interface ServiceDiscoveryListener {
        void onServiceFound(String serviceName, int port, Map<String, byte[]> attributes);
        void onServiceLost(String serviceName);
    }

    public NsdClient(Context context, String serviceType, ServiceDiscoveryListener listener) {
        this.nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        this.serviceType = serviceType;
        this.listener = listener;
        initializeListeners();
    }

    private void initializeListeners() {
        // Initializes a listener for resolving discovered services
        resolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed: " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.i(TAG, "Service Resolved: " + serviceInfo);
                InetAddress host = serviceInfo.getHost();
                int port = serviceInfo.getPort();
                String serviceName = serviceInfo.getServiceName();
                Map<String, byte[]> attributes = serviceInfo.getAttributes();
                if (listener != null) {
                    listener.onServiceFound(serviceName, port, attributes);
                }
            }
        };

        // Initializes a listener for discovering available services
        discoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service found: " + serviceInfo.getServiceType());
                // Only resolve services of the desired type
                if (serviceInfo.getServiceType().equals(serviceType)) {
                    Log.d(TAG, "Attempting to resolve service: " + serviceInfo.getServiceName());
                    if (serviceInfo.getServiceName().equals("UBSAuth")) {
                        Map<String,byte[]> attributes = serviceInfo.getAttributes();
                        for (Map.Entry<String, byte[]> item: attributes.entrySet()) {
                            String chave = item.getKey();
                            String valor = null;
                            try {
                                valor = new String(item.getValue(), "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                            Log.d(TAG, "Encontrada chave: " + chave + " com valor: " + valor);
                        }
                        nsdManager.resolveService(serviceInfo, resolveListener);
                    }
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Service lost: " + serviceInfo.getServiceName());
                if (listener != null) {
                    listener.onServiceLost(serviceInfo.getServiceName());
                }
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed to start: Error code " + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed to stop: Error code " + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public void startDiscovery() {
        // Stop any previous discovery sessions to prevent conflicts
        try {
            stopDiscovery();
        } catch (Exception e) {
            Log.d(TAG, "Serviço não estava rodando... continuando...");
        }
        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
        Log.d(TAG, "Starting service discovery for type: " + serviceType);
    }

    public void stopDiscovery() {
        if (discoveryListener != null) {
            try {
                nsdManager.stopServiceDiscovery(discoveryListener);
                Log.d(TAG, "Stopping service discovery");
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Discovery was not running or listener is invalid");
            }
        }
    }
}
