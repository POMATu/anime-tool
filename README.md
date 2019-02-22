# Anime tool

I wrote this script cause I got really annoyed by typing console commands everytime I wan't to watch next anime episode with external subs or external audio file. 

Before that time when I used VLC instead of mpv it was even more painful to do all that shit through GUI.

# How it works

It searches automatically for subtitles and external audio even if files are named really differently then starts MPV player with correct arguments.

Example subtitle name:
```Psycho-pass - 01 [BDRip 1920x1080 x264 FLAC].ass```

Example video file name:
```[bonkai77].Psycho-Pass.Episode.01.Crime.Coefficient.1080p.Dual.Audio.Bluray [14BEE8C1].mkv```

And yeah it will figure out all shit even in this case! And link each episode to each external file without any pain in your ass!

# Requirements
* perl (you have it already installed in your linux)
* mpv player
`sudo apt install mpv`

# Usage

For external audio:
```
$ anime-tool -audio '/path/to/video/folder' '/path/to/audio/folder' EPISODE_NUMBER
```

For external subtitles:
```
$ anime-tool -subs '/path/to/video/folder' '/path/to/subs/folder' EPISODE_NUMBER
```

For both:
```
$ anime-tool -both '/path/to/video/folder' '/path/to/audio/folder' '/path/to/subs/folder' EPISODE_NUMBER
```

Example:
```
$ anime-tool -subs '~/Downloads/yoi-collection' '~/Downloads/yoi-collection/subs-rus' 69
```

# Warning

Don't forget to specify episode number with LEADING ZEROES like your videofiles are called.

# Design

I impelemented an insane perl subroutine that called ```insaneFindFiles```

It iterates through every symbol in filename untill it reaches number then tests if that number got's incremented on the next filename. If not - it seeks for next number in filename untill it finds the right position.

It works cause all files are numbered identically when you download multiple episodes of one title. It will work even if you have subtitles from one source and videos from another source. Numbers are still there anyway.

# Bugs

If you find any bug feel free to create an issue. I'll need your filenames as input, maybe I missed something and it will fail on some input data. But I tried to prevent any possible caveats that I could imagine with my mind.

I am not sure if it will work if episodes are named 1,2,3,4,5,6,7,8,9,10 but actually I haven't seen that naming for really long time.

