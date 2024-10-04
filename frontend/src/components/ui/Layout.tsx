import { Outlet } from 'react-router-dom'
import NavbarWithLabel from '@components/navbar/NavbarWithLabel'
import { PropsWithChildren } from 'react'
export default function Layout({ children }: PropsWithChildren) {
  return (
    <div className='w-full h-full bg-gradient'>
      <div className='w-full min-h-dvh'>
        <div className='sm:w-[37.5rem] w-full mx-auto min-h-dvh sm:border-l sm:border-r sm:border-white'>
          <NavbarWithLabel />
          <Outlet />
          {children}
        </div>
      </div>
    </div>
  )
}
