import SvgEditIcon from '@assets/svg/review/EditIcon'

type MyComponent = {
  className?: string
  onClick?: () => void
}

type NavContent = {
  label: string
  Component?: React.ComponentType<MyComponent>
  navigate?: string
  text?: string
}

type PathToNavContent = {
  [key: string]: NavContent
}

const pathToNavContent: PathToNavContent = {
  '/notification': {
    label: '알림',
    text: '모두 읽음',
  },
  '/calendar': { label: '캘린더' },
  '/observing-spot': { label: '관측지' },
  '/mypage': { label: 'MY' },
  '/mypage/profile': { label: 'MY' },
  '/mypage/nickname': { label: 'MY' },
  '/mypage/location': { label: 'MY' },
  '/mypage/review': { label: 'MY' },
  '/mypage/reaction': { label: 'MY' },
  '/review': { label: '후기', Component: SvgEditIcon, navigate: '/review/new' },
  '/review/new': { label: '후기 작성' },
}

export { pathToNavContent }
