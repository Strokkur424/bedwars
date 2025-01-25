package net.strokkur.bedwars.paper;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class BedwarsLoader implements PluginLoader {

    @Override
    public void classloader(PluginClasspathBuilder builder) {
        final MavenLibraryResolver maven = new MavenLibraryResolver();
        maven.addRepository(new RemoteRepository.Builder("Central Repository", "default", "https://repo1.maven.org/maven2/").build());

        maven.addDependency(new Dependency(new DefaultArtifact("org.spongepowered:configurate-yaml:4.1.2"), null));

        builder.addLibrary(maven);
    }
}
