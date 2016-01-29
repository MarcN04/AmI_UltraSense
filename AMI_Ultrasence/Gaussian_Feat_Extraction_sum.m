close all
%clear

fs=44100;
[b,a] = butter(6, .6, 'high');
file = './audio/new_ref/up_down_s3.wav';
%file = './audio/swipe_right_hand_l_r_s3_rec.wav';
%file = './audio/treshold_det/n7/up_down_rec.wav';
%file = './audio/activity/bed_walking_away/bed_parallel_5.wav';
%file = './audio/activity/bed_falling/fall_soft_9.wav';
%file = './audio/activity/bed_approaching/appr_diag_1.wav';
%file = './audio/activity/bed_moving.wav';
file = './audio/activity/working_walking_back_and_forth/working_no_voice.wav';
%file = './audio/NB_walking_rec.wav';

file = './audio/UltraSense/knock2_rec.wav';


% fileDir = './audio/activity/bed_falling/new/';
% files = dir(strcat(fileDir, 'f*.wav*'));
% x = [];
% for i=1:length(files)
%     temp = wavread(strcat(fileDir, files(i).name));
%     temp = temp(:,1);
%     temp = filter(b,a,temp);
%     x = [x;temp];
% end

%read file
x = wavread(file);
x = x(:, 1);
% x = filter(b,a,x);

%x = x(10*fs:14*fs);

%csvwrite('up_down_s3.txt', x);

%parameters for stft
wlen = 1024; %equals nfft
hopSize = wlen/2;
S=[];
counter=1;
dFactor=1;

[s,f,t] = complete_stft(x, wlen, hopSize, 4096, fs);
show_stft(s,f,t);

%figure;
for i=1:hopSize:length(x)-wlen
    xk = x(i:i+wlen-1);
    win = hann(wlen, 'periodic');
    xk = xk.*win;
    XK = fft(xk, wlen);
    XK = XK(1:wlen/2+1);
    XK = abs(XK);
    XK = XK/wlen/(sum(win)/wlen);
    XK(2:end-1,:) = XK(2:end-1,:).*2;
    XK = 20*log10(XK+1e-6);
    %XK = imfilter(XK, fspecial('gaussian', [5 1], 5.0), 'replicate');
    f = (0:ceil((1+wlen)/2)-1)*(fs/dFactor)/wlen;
    f_ind = find(f > 0 & f < 1.2e4);
%     f_ind = find(f > 1.95e4 & f < 2.05e4);
    %plot(f(f_ind), XK(f_ind));
    %axis([1.95e4 2.05e4 -120 -70]);
    S(:, counter) = XK(f_ind);
    counter = counter+1;
end

%calibrated gesture paras for noisy environments
% carrierRow = size(S,1)/2;
% threshold = -50; %60db for test detection!
% feat_high_threshold = 3;
% feat_low_threshold = 2;
% halfCarrierWidth = 3;
% slackWidth=1;
% useSecondPeakDetection=0;
% feat_max_threshold=20;

%calibrated gesture paras
% carrierRow = size(S,1)/2;
% threshold = -55; %60db for test detection!
% feat_high_threshold = 3;
% feat_low_threshold = 2;
% halfCarrierWidth =4;
% slackWidth=0;
% useSecondPeakDetection=0;
% feat_max_threshold=10000;


%parameters for feature detection
% %used on workdesk presense detector
% carrierRow = size(S,1)/2;
% threshold = -60;
% feat_high_threshold = 1;
% feat_low_threshold = .5;
% halfCarrierWidth = 5;
% slackWidth=10;
% useSecondPeakDetection=0;
% feat_max_threshold=8;

%parameters for feature detection (bed_fall)
carrierRow = size(S,1)/2;
threshold = -70;
feat_high_threshold = 3;
feat_low_threshold = 1.5;
halfCarrierWidth = 5;
slackWidth=5;
useSecondPeakDetection=0;
feat_max_threshold=15;

%spectrogram thresholded
S2 = S;
S2 (S2 < threshold) = -120;
figure;
t = (wlen/2:hopSize:length(x)-wlen/2-1)/fs;
imagesc(t, f(f_ind), S2);

%t = 1:size(S,2);
[feat, meansPerCol] = extract_feat(S, t, carrierRow, threshold, feat_max_threshold, feat_high_threshold, feat_low_threshold, halfCarrierWidth, slackWidth, useSecondPeakDetection, ones(1, size(S,2)), ones(1,size(S,2)));

%dist = getDistanceBetween(feat)
%mu = mean(dist)
%sigma = std(dist)
feat2 = [];
for i=1:size(feat,2)
    if(feat(2,i) > 0)
        feat2 = [feat2 feat(:,i)];
    end
end

%vorher nicht auskommentiert gewesen
%feat2


%feat = feat(:,6:end);
%output features for learning (need to adjust label)
%0 = down
%1 = up
%2 = swipe_R_r_l_down
%3 = swipe_R_r_l_up
%4 = swipe_R_l_r_down
%5 = swipe_R_l_r_up
%6 = noise;
label = 5;
%csvwrite('swipe_R_l_r_up.txt', [feat' (label*ones(size(feat,2), 1))]);


%plot the features and the extracted mean
figure;
hold on;
t2=0:.01:t(end);
for i=1:size(feat, 2)
  Z = feat(3,i)*normpdf(t2, feat(1,i), feat(2,i));
  plot(t2,Z);
end

%plot means
plot(t, meansPerCol(1,:), 'r');
plot(t, meansPerCol(2,:).*(-1), 'r');
axis([t(1) t(end) -10 10]);
%xlabel('Timesteps')
%ylabel('Frequency bins')

% figure;
% x = x/(max(abs(x)));
% y = [];
% for i=1:10:size(x,1)-10
%     temp = x(i:i+9);
%     temp = temp(temp > 0);
%     y = [y mean(temp)];
% end
% plot(y);

[n,m] = size(S);

vecA = [];
peakDuration = 0;
peakIntegral = 0;
peakMaximum = 0;

for i = 1:m

  vecA = [vecA sum(S(:,i))];

    if (vecA(i) > -20000) 
      peakDuration = peakDuration + 1;
      peakIntegral = peakIntegral + vecA(i);
    end   

end
peakMaximum = max(vecA);
peakDuration = peakDuration * 1024 * 1/fs;

figure;
plot(t,vecA);

peakDuration
peakIntegral
peakMaximum