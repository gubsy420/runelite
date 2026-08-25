/*
 * Copyright (c) 2026
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.externalplugins;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.net.URLClassLoader;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.util.ReflectUtil;

/**
 * Classloader for jars in {@link net.runelite.client.RuneLite#SIDELOADED_PLUGINS_DIR}.
 * <p>
 * Loading a sideloaded plugin through a plain {@link URLClassLoader} works, but leaves it in a
 * unique unnamed module the client can't get a {@code MODULE|PRIVATE} lookup into. {@link
 * net.runelite.client.eventbus.EventBus} needs one to build a {@link java.lang.invoke.LambdaMetafactory}
 * call site for each {@code @Subscribe} method, so every one of them fell back to reflective
 * dispatch and logged a warning on registration. Implementing {@link ReflectUtil.PrivateLookupableClassLoader}
 * and installing the lookup helper is what {@link PluginHubClassLoader} does for the same reason.
 */
class SideloadedPluginClassLoader extends URLClassLoader implements ReflectUtil.PrivateLookupableClassLoader
{
	@Getter
	@Setter
	private MethodHandles.Lookup lookup;

	SideloadedPluginClassLoader(URL[] urls)
	{
		super(urls, SideloadedPluginClassLoader.class.getClassLoader());
		ReflectUtil.installLookupHelper(this);
	}

	@Override
	public Class<?> defineClass0(String name, byte[] b, int off, int len) throws ClassFormatError
	{
		return super.defineClass(name, b, off, len);
	}
}
