param([Parameter(Mandatory=$true)][string]$InputManifest)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
# Package the generated raster assets: remove the neutral preview matte, preserve
# foreground coverage during downsampling, and write hard-alpha game-size PNGs.
Add-Type -ReferencedAssemblies System.Drawing.Common,System.Drawing.Primitives,System.Private.Windows.GdiPlus,System.Private.Windows.Core -TypeDefinition @'
using System;
using System.Drawing;
using System.Drawing.Imaging;
public static class NecromancerArtImport {
    static bool Foreground(Color c) {
        int hi = Math.Max(c.R, Math.Max(c.G, c.B)), lo = Math.Min(c.R, Math.Min(c.G, c.B));
        return c.A >= 128 && !(lo >= 220 && hi - lo <= 12);
    }
    public static void Import(string input, string output, int width, int height, int padding) {
        using var source = new Bitmap(input);
        int left=source.Width, top=source.Height, right=0, bottom=0;
        for(int y=0;y<source.Height;y++) for(int x=0;x<source.Width;x++) {
            if(!Foreground(source.GetPixel(x,y))) continue;
            left=Math.Min(left,x); top=Math.Min(top,y); right=Math.Max(right,x); bottom=Math.Max(bottom,y);
        }
        if(left>right || top>bottom) throw new Exception("Generated asset has no foreground: " + input);
        using var result = new Bitmap(width,height,PixelFormat.Format32bppArgb);
        for(int y=padding;y<height-padding;y++) for(int x=padding;x<width-padding;x++) {
            int n=0,r=0,g=0,b=0;
            for(int sy=0;sy<4;sy++) for(int sx=0;sx<4;sx++) {
                int ix=left+(int)((x-padding+(sx+.5)/4)*(right-left+1)/(width-2*padding));
                int iy=top+(int)((y-padding+(sy+.5)/4)*(bottom-top+1)/(height-2*padding));
                var c=source.GetPixel(Math.Min(ix,right),Math.Min(iy,bottom));
                if(!Foreground(c)) continue;
                n++;r+=c.R;g+=c.G;b+=c.B;
            }
            if(n>=3) result.SetPixel(x,y,Color.FromArgb(255,r/n,g/n,b/n));
        }
        result.Save(output,ImageFormat.Png);
    }
}
'@
$projectRoot = Split-Path -Parent $PSScriptRoot
$inputs = Get-Content -LiteralPath $InputManifest -Raw | ConvertFrom-Json
foreach ($entry in $inputs.PSObject.Properties) {
    $id = $entry.Name
    $width=32; $height=32; $padding=2
    $target = "src/main/resources/assets/shouyun_workshop/textures/item/${id}_necromancer_staff.png"
    if ($id -in @('panel','soul','cooldown')) {
        $width=16; $height=16; $padding=1
        $target="src/main/resources/assets/shouyun_workshop/textures/gui/necromancer_${id}.png"
        if ($id -eq 'panel') { $width=160; $height=80; $padding=0 }
    }
    [NecromancerArtImport]::Import($entry.Value, (Join-Path $projectRoot $target), $width, $height, $padding)
    Write-Output "$id : ${width}x${height} RGBA"
}
