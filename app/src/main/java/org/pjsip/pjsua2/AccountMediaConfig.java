/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public class AccountMediaConfig extends PersistentObject {
  private transient long swigCPtr;

  protected AccountMediaConfig(long cPtr, boolean cMemoryOwn) {
    super(pjsua2JNI.AccountMediaConfig_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(AccountMediaConfig obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        pjsua2JNI.delete_AccountMediaConfig(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public void setTransportConfig(TransportConfig value) {
    pjsua2JNI.AccountMediaConfig_transportConfig_set(swigCPtr, this, TransportConfig.getCPtr(value), value);
  }

  public TransportConfig getTransportConfig() {
    long cPtr = pjsua2JNI.AccountMediaConfig_transportConfig_get(swigCPtr, this);
    return (cPtr == 0) ? null : new TransportConfig(cPtr, false);
  }

  public void setLockCodecEnabled(boolean value) {
    pjsua2JNI.AccountMediaConfig_lockCodecEnabled_set(swigCPtr, this, value);
  }

  public boolean getLockCodecEnabled() {
    return pjsua2JNI.AccountMediaConfig_lockCodecEnabled_get(swigCPtr, this);
  }

  public void setStreamKaEnabled(boolean value) {
    pjsua2JNI.AccountMediaConfig_streamKaEnabled_set(swigCPtr, this, value);
  }

  public boolean getStreamKaEnabled() {
    return pjsua2JNI.AccountMediaConfig_streamKaEnabled_get(swigCPtr, this);
  }

  public void setSrtpUse(int value) {
    pjsua2JNI.AccountMediaConfig_srtpUse_set(swigCPtr, this, value);
  }

  public int getSrtpUse() {
    return pjsua2JNI.AccountMediaConfig_srtpUse_get(swigCPtr, this);
  }

  public void setSrtpSecureSignaling(int value) {
    pjsua2JNI.AccountMediaConfig_srtpSecureSignaling_set(swigCPtr, this, value);
  }

  public int getSrtpSecureSignaling() {
    return pjsua2JNI.AccountMediaConfig_srtpSecureSignaling_get(swigCPtr, this);
  }

  public void setSrtpOpt(SrtpOpt value) {
    pjsua2JNI.AccountMediaConfig_srtpOpt_set(swigCPtr, this, SrtpOpt.getCPtr(value), value);
  }

  public SrtpOpt getSrtpOpt() {
    long cPtr = pjsua2JNI.AccountMediaConfig_srtpOpt_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SrtpOpt(cPtr, false);
  }

  public void setIpv6Use(int value) {
    pjsua2JNI.AccountMediaConfig_ipv6Use_set(swigCPtr, this, value);
  }

  public int getIpv6Use() {
    return pjsua2JNI.AccountMediaConfig_ipv6Use_get(swigCPtr, this);
  }

  public void setRtcpMuxEnabled(boolean value) {
    pjsua2JNI.AccountMediaConfig_rtcpMuxEnabled_set(swigCPtr, this, value);
  }

  public boolean getRtcpMuxEnabled() {
    return pjsua2JNI.AccountMediaConfig_rtcpMuxEnabled_get(swigCPtr, this);
  }

  public void setRtcpFbConfig(RtcpFbConfig value) {
    pjsua2JNI.AccountMediaConfig_rtcpFbConfig_set(swigCPtr, this, RtcpFbConfig.getCPtr(value), value);
  }

  public RtcpFbConfig getRtcpFbConfig() {
    long cPtr = pjsua2JNI.AccountMediaConfig_rtcpFbConfig_get(swigCPtr, this);
    return (cPtr == 0) ? null : new RtcpFbConfig(cPtr, false);
  }

  public AccountMediaConfig() {
    this(pjsua2JNI.new_AccountMediaConfig(), true);
  }

  public void readObject(ContainerNode node) throws java.lang.Exception {
    pjsua2JNI.AccountMediaConfig_readObject(swigCPtr, this, ContainerNode.getCPtr(node), node);
  }

  public void writeObject(ContainerNode node) throws java.lang.Exception {
    pjsua2JNI.AccountMediaConfig_writeObject(swigCPtr, this, ContainerNode.getCPtr(node), node);
  }

}
