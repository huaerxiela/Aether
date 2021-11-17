package cn.demo.appq.Interceptor;

import android.support.annotation.NonNull;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.github.megatronking.netbare.http.HttpBody;
import com.github.megatronking.netbare.http.HttpRequest;
import com.github.megatronking.netbare.http.HttpRequestHeaderPart;
import com.github.megatronking.netbare.http.HttpResponse;
import com.github.megatronking.netbare.http.HttpResponseHeaderPart;
import com.github.megatronking.netbare.injector.HttpInjector;
import com.github.megatronking.netbare.injector.InjectorCallback;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import cn.demo.appq.App;
import cn.demo.appq.entity.ReqEntity;
import cn.demo.appq.greendao.ReqEntityDao;
import cn.demo.appq.utils.DBManager;
import cn.demo.appq.utils.IOUtils;

public class NetHttpInject implements HttpInjector {
    private static final String TAG = "TestHttpInject";
    private static final boolean RECORD_REQUEST_BODY = true;

    @Override
    public boolean sniffRequest(@NonNull HttpRequest request) {
        return true;
    }

    @Override
    public boolean sniffResponse(@NonNull HttpResponse response) {
        return true;
    }

    @Override
    public void onRequestInject(@NonNull HttpRequestHeaderPart header, @NonNull InjectorCallback callback) throws IOException {
        callback.onFinished(header);
    }

    @Override
    public void onResponseInject(@NonNull HttpResponseHeaderPart header, @NonNull InjectorCallback callback) throws IOException {
        callback.onFinished(header);
    }

    @Override
    public void onRequestInject(@NonNull HttpRequest request, @NonNull HttpBody body, @NonNull InjectorCallback callback) throws IOException {
        List<ReqEntity> reqEntities = DBManager.getInstance()
                .getReqEntityDao()
                .queryBuilder()
                .where(ReqEntityDao.Properties.SessionId.eq(request.id()))
                .list();
        String pkg = App.getProcessNameByUid(request.uid());
        Log.i("sanbo.url","pkg:"+pkg
                +"\r\n\t URL:"+request.url()
                +"\r\n\t protocol:"+request.protocol().toString()
                +"\r\n\t httpProtocol:"+request.httpProtocol().toString()
                +"\r\n\t method:"+request.method().name()
                +"\r\n\t isHttps:"+request.isHttps()
                +"\r\n\t Header:"+GsonUtils.toJson(request.requestHeaders())
        );
        if (reqEntities != null && reqEntities.size() > 0) {
            ReqEntity entity = reqEntities.get(0);
            if (RECORD_REQUEST_BODY){
                if (entity.getReqContent() == null) {
                    entity.setReqContent("");
                }
                ByteBuffer prebuffer = IOUtils.string2ByteBuffer(entity.getReqContent());
                ByteBuffer nextBuffer = body.toBuffer().asReadOnlyBuffer();
                ByteBuffer newBuffer = ByteBuffer.allocate(prebuffer.limit() + nextBuffer.limit());
                newBuffer.put(prebuffer);
                newBuffer.put(nextBuffer);
                if(newBuffer.limit()>=2048*1024*3.0f/4){
                    // SqlLite 单行最大容量是 2M
                    return;
                }
                //更新流量消耗大小
                entity.setLength(entity.getLength()+nextBuffer.limit());
                entity.setReqContent(IOUtils.byteBuffer2String(newBuffer));
                DBManager.getInstance().getReqEntityDao().update(entity);
            }
        } else {
            String packagename = App.getProcessNameByUid(request.uid());
            ReqEntity reqEntity = new ReqEntity(
                    null,
                    request.id(),
                    AppUtils.getAppName(packagename),
                    packagename,
                    AppUtils.getAppVersionName(packagename),
                    String.valueOf(AppUtils.getAppVersionCode(packagename)),
                    request.url(),
                    request.host(),
                    request.port(),
                    0,
                    request.ip(),
                    request.protocol().toString(),
                    request.httpProtocol().toString(),
                    request.method().name(),
                    request.path(),
                    request.isHttps(),
                    request.time(),
                    request.uid(),
                    body.toBuffer().limit(),
                    request.streamId(),
                    GsonUtils.toJson(request.requestHeaders()),
                    null,
                    "",
                    "",
                    RECORD_REQUEST_BODY ? IOUtils.byteBuffer2String(body.toBuffer().asReadOnlyBuffer()) : null,
                    null,
                    null,
                    null,
                    null,
                    NetworkUtils.getNetworkType().name(),
                    request.requestBodyOffset(),
                    0);
            DBManager.getInstance().getReqEntityDao().insert(reqEntity);
        }
        callback.onFinished(body);
    }

    @Override
    public void onResponseInject(@NonNull HttpResponse response, @NonNull HttpBody body, @NonNull InjectorCallback callback) throws IOException {
        List<ReqEntity> reqEntities = DBManager.getInstance()
                .getReqEntityDao()
                .queryBuilder()
                .where(ReqEntityDao.Properties.SessionId.eq(response.id()))
                .list();

        if (reqEntities != null && reqEntities.size() > 0) {
            ReqEntity entity = reqEntities.get(0);
            entity.setRespCode(response.code());
            entity.setResponseHeaders(GsonUtils.toJson(response.responseHeaders()));
            entity.setResponseBodyOffset(response.responseBodyOffset());
            entity.setRequestBodyOffset(response.requestBodyOffset());
            if (RECORD_REQUEST_BODY) {
                if (entity.getRespContent() == null) {
                    entity.setRespContent("");
                }
                ByteBuffer prebuffer = IOUtils.string2ByteBuffer(entity.getRespContent());
                ByteBuffer nextBuffer = body.toBuffer().asReadOnlyBuffer();
                ByteBuffer newBuffer = ByteBuffer.allocate(prebuffer.limit() + nextBuffer.limit());
                newBuffer.put(prebuffer);
                newBuffer.put(nextBuffer);
                if(newBuffer.limit()>=2048*1024*3.0f/4){
                    // SqlLite 单行最大容量是 2M
                    return;
                }
                entity.setRespContent(IOUtils.byteBuffer2String(newBuffer));
            }
            entity.setRespMessage(response.message());
            entity.setIsWebSocket(response.isWebSocket());
            entity.setIndex(0);
            entity.setLength(entity.getLength() + body.toBuffer().limit());
            DBManager.getInstance().getReqEntityDao().update(entity);
        }
        callback.onFinished(body);
    }

    @Override
    public void onRequestFinished(@NonNull HttpRequest request) {
        Log.d(TAG, "onRequestFinished: " + request.uid());
    }

    @Override
    public void onResponseFinished(@NonNull HttpResponse response) {
        Log.d(TAG, "onRequestFinished: " + response.uid());
    }
}
