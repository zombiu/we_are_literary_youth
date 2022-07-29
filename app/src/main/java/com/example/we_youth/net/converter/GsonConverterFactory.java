/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.we_youth.net.converter;

import static com.example.we_youth.net.converter.ResponseKt.FULL_RESULT;
import static com.example.we_youth.net.converter.ResponseKt.ONLY_DATA;

import com.blankj.utilcode.util.LogUtils;
import com.example.we_youth.net.BaseJson;
import com.example.we_youth.net.WanApiResponse;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * A {@linkplain Converter.Factory converter} which uses Gson for JSON.
 *
 * <p>Because Gson is so flexible in the types it supports, this converter assumes that it can
 * handle all types. If you are mixing JSON serialization with something else (such as protocol
 * buffers), you must {@linkplain Retrofit.Builder#addConverterFactory(Converter.Factory) add this
 * instance} last to allow the other converters a chance to see their types.
 */
public final class GsonConverterFactory extends Converter.Factory {
    /**
     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static GsonConverterFactory create() {
        return create(new Gson());
    }

    /**
     * Create an instance using {@code gson} for conversion. Encoding to JSON and decoding from JSON
     * (when no charset is specified by a header) will use UTF-8.
     */
    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static GsonConverterFactory create(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        return new GsonConverterFactory(gson);
    }

    private final Gson gson;

    private GsonConverterFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(
            Type type, Annotation[] annotations, Retrofit retrofit) {
        LogUtils.e("-->>" + type);
        int respType = ONLY_DATA;
        for (int i = 0; i < annotations.length; i++) {
            LogUtils.e("-->>" + annotations[i]);
            if (annotations[i] instanceof Response) {
                respType = ((Response) annotations[i]).value();
                break;
            }
        }
        if (respType == ONLY_DATA) {
            ParameterizedTypeImpl impl = new ParameterizedTypeImpl(WanApiResponse.class, new Type[]{type});
            TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(impl));
            return new GsonResponseBodyConverter<>(gson, adapter);
        } else if (respType == FULL_RESULT) {
            TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
            return new GsonResponseBodyToBaseJsonConverter<>(gson, adapter);
        }
        return null;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(
            Type type,
            Annotation[] parameterAnnotations,
            Annotation[] methodAnnotations,
            Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonRequestBodyConverter<>(gson, adapter);
    }
}

// ParameterizedType 简单说来就是形如“ 类型<> ”的类型，如:Map<String,User>
// public interface ParameterizedType extends Type {
//     // 返回Map<String,User>里的String和User，所以这里返回[String.class,User.clas]
//    Type[] getActualTypeArguments();
//    // Map<String,User>里的Map,所以返回值是Map.class
//    Type getRawType();
//    // 用于这个泛型上中包含了内部类的情况,一般返回null
//    Type getOwnerType();
//}