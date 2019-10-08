# Disclaimer

This project is deprecated and also bullshit. I wasnt able to do that with different algorythms neither regexp. Humanities people name files so unpredictable and randomly, sometimes even with mistakes or incorrect roman numbers (IIII) making it impossible to parse with any software.

I think I will return to this problem and try making AI Deep Learning software for achieving this goal. Should be best task to learn AI development, it's just a parsing of text.

So for now please dont use this shit, you have been warned.

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

Either will work. You can specify audio, subs or both as argument. Order doesn't matter. `=` sign is not required.
You can also use shorthand arguments for bash autocompletion.
Movie (default current directory) and episode (default first found) are optional, but either audio or subtitles are required.
```
anime-tool --movie="(movie)" --audio="(audio)" --subs="(subs)" --episode="(episode)"
anime-tool -movie="(movie)" -audio="(audio)" -subs="(subs)" -episode="(episode)"
anime-tool -m="(movie)" -a="(audio)" -s="(subs)" -e="(episode)"
anime-tool -m (movie) -a (audio) -s (subs) -e (episode)

anime-tool -a (audio) -s (subs)
```

Example
```
cd /downloads/anime
anime-tool -a ./RUS_sound -s ./subs\ rus -e 01
```

# Warning

Don't forget to specify episode number with LEADING ZEROES like your videofiles are called.

# Why MPV?

For my experience it's the best player so far. I have not really old PC, my videocard is also really fine, however while using VLC I experienced slow-motion lags while watching 10bit 1080p rips. MPV seems to have hardware acceleration and works really fine even when I have 1000 windows with IDE opened.

# MPV tips

* Adjust subtitle delay by +/- 0.1 seconds: 'z' and 'x'
* Cycle through the available audio tracks: '#'
* Cycle through the available subtitles: 'j'
* Toggle subtitle visibility: 'v'
* Toggle fullscreen: 'f'
* Exit fullscreen: 'ESC'
* Adjust subtitle delay so that the next or previous subtitle is displayed now: 'Ctrl+Shift+Left' and 'Ctrl+Shift+Right'

# Design

I impelemented an insane perl subroutine that called ```insaneFindFiles```

It iterates through every symbol in filename untill it reaches number then tests if that number got's incremented on the next filename. If not - it seeks for next number in filename untill it finds the right position.

It works cause all files are numbered identically when you download multiple episodes of one title. It will work even if you have subtitles from one source and videos from another source. Numbers are still there anyway.

UPD: Now it also filters all openings bonuses and other crap from filelist successfully. Now it supports names with different anime id in the name like that

```
Dropping: [Zurako]_Dantalian_no_Shoka_NCED_(BD_720p_AAC)_[3D87EDEC].mkv
Dropping: [Zurako]_Dantalian_no_Shoka_NCOP_(BD_720p_AAC)_[EC109C5F].mkv
```

# Sample trace

So that's was the most interesting and buggy filetree. Notice that it have 2 additional bonus files named differently and also series are having unique id.

```
./[Zurako]_Dantalian_no_Shoka_07_(BD_720p_AAC)_[6DE45D1C].mkv
./[Zurako]_Dantalian_no_Shoka_05_(BD_720p_AAC)_[54F8BE5B].mkv
./[Zurako]_Dantalian_no_Shoka_NCED_(BD_720p_AAC)_[3D87EDEC].mkv
./[Zurako]_Dantalian_no_Shoka_04_(BD_720p_AAC)_[444EED7E].mkv
./[Zurako]_Dantalian_no_Shoka_06_(BD_720p_AAC)_[01AC9B57].mkv
./[Zurako]_Dantalian_no_Shoka_11_(BD_720p_AAC)_[FC0B2E19].mkv
./[Zurako]_Dantalian_no_Shoka_08_(BD_720p_AAC)_[EBA836CC].mkv
./[Zurako]_Dantalian_no_Shoka_01_(BD_720p_AAC)_[AF5EEB7F].mkv
./[Zurako]_Dantalian_no_Shoka_12_(BD_720p_AAC)_[8981728F].mkv
./[Zurako]_Dantalian_no_Shoka_02_(BD_720p_AAC)_[382AEE80].mkv
./[Zurako]_Dantalian_no_Shoka_10_(BD_720p_AAC)_[31DB05D4].mkv
./[Zurako]_Dantalian_no_Shoka_NCOP_(BD_720p_AAC)_[EC109C5F].mkv
```
### Pass 1: Generating mask for filtering
* Replacing all numbers with '/': `./[Zurako]_Dantalian_no_Shoka_//_(BD_///p_AAC)_[/D///D/C].mkv`
* Calculating common mask scopes: `[Zurako]_Dantalian_no_Shoka_//_(BD_///p_AAC)_[`, `[Zurako]_Dantalian_no_Shoka_NC`
* Comparing masks with each other, calculating weights for each found scope: 
```
Scope length: 28 Weight: 2
Scope length: 46 Weight: 11
```
* Assuming that biggest weight is the rightest one
* Using that as reference mask
* Cutting all masks to reference mask length
* Comparing reference mask with all mask list again, dropping not equals masks
```
Dropping: [Zurako]_Dantalian_no_Shoka_NCED_(BD_720p_AAC)_[3D87EDEC].mkv
Dropping: [Zurako]_Dantalian_no_Shoka_NCOP_(BD_720p_AAC)_[EC109C5F].mkv
```

### Pass 2: Doing same shit backwards
* Same stuff again, but we mirror all masks to compare them from right to left
```
Scope length: 5 Weight: 13
Heaviest scope: 5
Reference mask: vkm.]
```
* So now it can't drop anything cause there are just one mask with one weight

### Pass 3: Comparing results
* Taking smallest filelist as right result.
* If filtering don't succeed - it just does nothing. Example unsuccessful filtering names (completely different file length and naming)
```
./04 - Accomplishment.mkv
./05 - Getaway.mkv
./11 - Future.mkv
./10 - Joy.mkv
./01 - Flashing Before My Eyes.mkv
```
* Mask: `// - `

### Pass 4: converting Roman numerals to arabic numerals
* Preserving original filenames to stack
* Generating Roman numerals array (1-3999)
* Sorting Roman numerals from highest length to lowest with preceeding zeroes of maximum Roman numerals length so filenames length gonna be simmilar
* Replacing uppercase and lowercase Roman numerals in all filename (filename get's human-unreadable format, but we not gonna read that, script can still process that fine)
```
[Zurako]__000000000000500_anta_000000000000051_an_no_Shoka_01_(B_000000000000500__720p_AA_000000000000100_)_[AF5EEB7F]._000000000001000_k_000000000000005_
```

### Pass 5: Calculating number position
* Going all filenames from left to right `./[Zurako]_Dantalian_no_Shoka_07_(BD_720p_AAC)_[6DE45D1C].mkv`
* Found starting position of number fragment, that's a right number actually in this case
* Found ending position of number fragment
* Testing if number increments each iteration
* If not - going forward
* If we reach end of file name - resetting and trying again

### Pass 6: Validation
* Testing that found number is different in each filename, if yes - returning hash
* If not - sorting filelist backwards and trying again but now decreasing number every iteration (that's a workaround if we still have some wrong shit left in filelist even after filtering it may work if it that garbage-filenames are not completely randomized)

### Restoring original filenames from stack
* Getting original not messed up filenames, but now we know it's numbers

### Doing same stuff for subs/audio
* But in this case it's not important to filter wrong files, cause all we need is numbered hash. If it contains extra values - no problem that's still won't be used. But filtering still applied to all that, it's completely same routine

### Launching mpv finally

# Bugs

If you find any bug feel free to create an issue. I'll need your filenames as input, maybe I missed something and it will fail on some input data. But I tried to prevent any possible caveats that I could imagine with my mind.

I am not sure if it will work if episodes are named 1,2,3,4,5,6,7,8,9,10 but actually I haven't seen that naming for really long time.

