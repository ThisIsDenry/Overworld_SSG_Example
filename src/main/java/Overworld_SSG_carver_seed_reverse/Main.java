package Overworld_SSG_carver_seed_reverse;

import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.CPos;
import kaptainwutax.seedutils.util.math.DistanceMetric;
import kaptainwutax.seedutils.util.math.Vec3i;

import java.util.ArrayList;

public class Main {

    public static void main (String[] args) {
        ChunkRand chunkRand = new ChunkRand();

        ArrayList<Long> structureSeeds = new ArrayList<>();
        // brute force through every structure seed
        for (long structureSeed = 0; structureSeed < 1L << 48; structureSeed++) {
            // check for ravines at chunk 0,0
            RavineProperties rp = new RavineProperties(structureSeed, new CPos(0, 0));
            // generate() will return true if ravine succceds in generating (ravine can generate in that chunk)
            // generate() will return false if not - we move on to next structure seed;
            if (!rp.generate(chunkRand)) continue;

            // check if ravine is wide
            if (rp.width > 5.5) {
                structureSeeds.add(structureSeed);
                if (structureSeeds.size() > 9) break;
            }
        }

        // every minecraft seed has a structure seed: bottom 48 bits, but top 16 bits determine biome
        // biome affects spawn point, so we have to find a good top 16 bits (biome seed) with a good spawn (near (0,0))
        for (long structureSeed : structureSeeds) {
            for (long biomeSeed = 0; biomeSeed < 1L << 16; biomeSeed++) {
                long worldSeed = biomeSeed<<48|structureSeed;
                OverworldBiomeSource overworldBiomeSource = new OverworldBiomeSource(MCVersion.v1_16_1, worldSeed);
                BPos spawnPoint = overworldBiomeSource.getSpawnPoint();
                double distance = spawnPoint.distanceTo(new Vec3i(0, 0, 0), DistanceMetric.EUCLIDEAN);
                if (distance < 50.0) {
                    System.out.println(worldSeed);
                    // only one world seed per structure seed - one world seed per ravine
                    // can remove this if needed
                    break;
                }

                // can use this to find forest at 0,0:
                // overworldBiomeSource.getBiome(x, y, z)
            }
        }
    }
}
