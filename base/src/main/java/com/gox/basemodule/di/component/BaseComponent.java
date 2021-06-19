package com.gox.basemodule.di.component;

import com.gox.basemodule.repositary.BaseRepository;
import com.gox.basemodule.di.modules.NetworkModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {NetworkModule.class})
public interface BaseComponent {
    void inject(BaseRepository respositary);
}
